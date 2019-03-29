package com.sequenceiq.cloudbreak.service.workspace;

import static com.sequenceiq.cloudbreak.api.endpoint.v4.workspace.responses.WorkspaceStatus.DELETED;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Sets;
import com.sequenceiq.cloudbreak.api.endpoint.v4.workspace.requests.ChangeWorkspaceUsersV4Request;
import com.sequenceiq.cloudbreak.authorization.ResourceAction;
import com.sequenceiq.cloudbreak.common.model.user.CloudbreakUser;
import com.sequenceiq.cloudbreak.controller.exception.BadRequestException;
import com.sequenceiq.cloudbreak.controller.exception.NotFoundException;
import com.sequenceiq.cloudbreak.domain.workspace.User;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.repository.workspace.UserRepository;
import com.sequenceiq.cloudbreak.repository.workspace.WorkspaceRepository;
import com.sequenceiq.cloudbreak.service.Clock;
import com.sequenceiq.cloudbreak.service.CloudbreakRestRequestThreadLocalService;
import com.sequenceiq.cloudbreak.service.TransactionService;
import com.sequenceiq.cloudbreak.service.TransactionService.TransactionExecutionException;
import com.sequenceiq.cloudbreak.service.TransactionService.TransactionRuntimeExecutionException;
import com.sequenceiq.cloudbreak.service.user.UserService;

@Service
public class WorkspaceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceService.class);

    @Inject
    private TransactionService transactionService;

    @Inject
    private WorkspaceRepository workspaceRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private WorkspaceModificationVerifierService verifierService;

    @Inject
    private CloudbreakRestRequestThreadLocalService restRequestThreadLocalService;

    @Inject
    private Clock clock;

    public Workspace create(User user, Workspace workspace) {
        try {
            return transactionService.required(() -> {
                Workspace createdWorkspace = workspaceRepository.save(workspace);
                if (CollectionUtils.isEmpty(workspace.getUsers())) {
                    workspace.setUsers(Sets.newHashSet());
                }
                workspace.getUsers().add(user);
                // create resource role for the new workspace in UMS
                return createdWorkspace;
            });
        } catch (TransactionExecutionException e) {
            if (e.getCause() instanceof DataIntegrityViolationException) {
                throw new BadRequestException(String.format("Workspace with name '%s' in your tenant already exists.",
                        workspace.getName()), e.getCause());
            }
            throw new TransactionRuntimeExecutionException(e);
        }
    }

    public Set<Workspace> retrieveForUser(User user) {
        return user.getWorkspaces();
    }

    public Workspace getDefaultWorkspaceForUser(User user) {
        return workspaceRepository.getByName(user.getUserName(), user.getTenant());
    }

    public Optional<Workspace> getByName(String name, User user) {
        return Optional.ofNullable(workspaceRepository.getByName(name, user.getTenant()));
    }

    public Optional<Workspace> getByNameForUser(String name, User user) {
        return Optional.ofNullable(workspaceRepository.getByName(name, user.getTenant()));
    }

    /**
     * Use this method with caution, since it is not authorized! Don!t use it in REST context!
     *
     * @param id id of Workspace
     * @return Workspace
     */
    public Workspace getByIdWithoutAuth(Long id) {
        Optional<Workspace> workspace = workspaceRepository.findById(id);
        if (workspace.isPresent()) {
            return workspace.get();
        }
        throw new IllegalArgumentException(String.format("No Workspace found with id: %s", id));
    }

    public Workspace getByIdForCurrentUser(Long id) {
        CloudbreakUser cloudbreakUser = restRequestThreadLocalService.getCloudbreakUser();
        User user = userService.getOrCreate(cloudbreakUser);
        return get(id, user);
    }

    public Workspace get(Long id, User user) {
        Optional<Workspace> workspace = user.getWorkspaces().stream().filter(workspaceItem -> workspaceItem.getId().equals(id)).findFirst();
        if (!workspace.isPresent()) {
            throw new NotFoundException("Cannot find workspace by user.");
        }
        return workspace.get();
    }

    public Set<User> removeUsers(String workspaceName, Set<String> userIds, User user) {
        Workspace workspace = getByNameForUserOrThrowNotFound(workspaceName, user);
        verifierService.authorizeWorkspaceManipulation(user, workspace, ResourceAction.MANAGE,
                "You cannot remove users from this workspace.");
        verifierService.ensureWorkspaceManagementForUserRemoval(workspace, userIds);
        try {
            return transactionService.required(() -> {
                Set<User> users = userService.getByUsersIds(userIds);
                verifierService.validateAllUsersAreAlreadyInTheWorkspace(workspace, users);
                verifierService.verifyDefaultWorkspaceUserRemovals(user, workspace, users);
                userRepository.deleteAll(users);
                return users;
            });
        } catch (TransactionExecutionException e) {
            throw new TransactionRuntimeExecutionException(e);
        }
    }

    public Set<User> addUsers(String workspaceName, Set<ChangeWorkspaceUsersV4Request> changeWorkspaceUsersV4Requests, User currentUser) {
        Workspace workspace = getByNameForUserOrThrowNotFound(workspaceName, currentUser);
        verifierService.authorizeWorkspaceManipulation(currentUser, workspace, ResourceAction.INVITE,
                "You cannot add users to this workspace.");
        Set<User> usersToAdd = changeWorkspaceUsersV4Requests.stream()
                .map(request -> userService.getByUserId(request.getUserId()))
                .collect(Collectors.toSet());
        verifierService.validateUsersAreNotInTheWorkspaceYet(workspace, usersToAdd);
        try {
            return transactionService.required(() -> {
                usersToAdd.stream()
                        .forEach(user -> user.getWorkspaces().add(workspace));
                return usersToAdd;
            });
        } catch (TransactionExecutionException e) {
            throw new TransactionRuntimeExecutionException(e);
        }
    }

    public Set<User> updateUsers(String workspaceName, Set<ChangeWorkspaceUsersV4Request> updateWorkspaceUsersJsons, User currentUser) {
        return null; // permissions can be updated in UMS
    }

    public Workspace getByNameForUserOrThrowNotFound(String workspaceName, User currentUser) {
        Optional<Workspace> workspace = getByNameForUser(workspaceName, currentUser);
        return workspace.orElseThrow(() -> new NotFoundException("Cannot find workspace with name: " + workspaceName));
    }

    public Set<User> changeUsers(String workspaceName, Set<ChangeWorkspaceUsersV4Request> newUserPermissions, User currentUser) {
        return null; // permissions can be updated in UMS
    }

    private void removeUsers(User currentUser, Workspace workspace, Map<String, User> newUsers, Map<String, User> oldUsers) {

        Set<String> usersIdsToBeDeleted = new HashSet<>(oldUsers.keySet());
        usersIdsToBeDeleted.removeAll(newUsers.keySet());
        Set<User> usersToBeDeleted = oldUsers.values().stream()
                .filter(u -> usersIdsToBeDeleted.contains(u.getUserId()))
                .collect(Collectors.toSet());
        verifierService.verifyDefaultWorkspaceUserRemovals(currentUser, workspace, usersToBeDeleted);

        userRepository.deleteAll(usersToBeDeleted);
    }

    private void updateUsers(User currentUser, Workspace workspace, Map<String, User> newUsers,
            Map<String, Set<String>> newPermissions, Map<String, User> oldUsers) {
        // permissions can be updated in UMS
    }

    private void addUsers(Workspace workspace, Set<User> newUsers, Set<User> oldUsers) {
        Set<User> usersToBeAdded = new HashSet<>(newUsers);
        usersToBeAdded.removeAll(oldUsers);
        workspace.getUsers().addAll(usersToBeAdded);
    }

    private Set<String> getUserIds(Set<ChangeWorkspaceUsersV4Request> userPermissions) {
        return userPermissions.stream().map(ChangeWorkspaceUsersV4Request::getUserId).collect(Collectors.toSet());
    }

    public Workspace deleteByNameForUser(String workspaceName, User currentUser, Workspace defaultWorkspace) {
        try {
            return transactionService.required(() -> {
                Workspace workspaceForDelete = getByNameForUserOrThrowNotFound(workspaceName, currentUser);
                verifierService.authorizeWorkspaceManipulation(currentUser, workspaceForDelete, ResourceAction.MANAGE,
                        "You cannot delete this workspace.");

                verifierService.checkThatWorkspaceIsDeletable(currentUser, workspaceForDelete, defaultWorkspace);
                setupDeletionDateAndFlag(workspaceForDelete);
                workspaceRepository.save(workspaceForDelete);
                LOGGER.debug("Deleted workspace: {}", workspaceName);
                return workspaceForDelete;
            });
        } catch (TransactionExecutionException e) {
            throw new TransactionRuntimeExecutionException(e);
        }
    }

    private void setupDeletionDateAndFlag(Workspace workspaceForDelete) {
        workspaceForDelete.setStatus(DELETED);
        workspaceForDelete.setDeletionTimestamp(clock.getCurrentTimeMillis());
    }

    public Workspace getForCurrentUser() {
        CloudbreakUser cloudbreakUser = restRequestThreadLocalService.getCloudbreakUser();
        User user = userService.getOrCreate(cloudbreakUser);
        return get(restRequestThreadLocalService.getRequestedWorkspaceId(), user);
    }
}
