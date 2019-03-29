package com.sequenceiq.cloudbreak.authorization;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.aspect.workspace.WorkspaceResourceType;
import com.sequenceiq.cloudbreak.auth.altus.GrpcAuthorizationClient;
import com.sequenceiq.cloudbreak.domain.workspace.User;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.domain.workspace.WorkspaceAwareResource;
import com.sequenceiq.cloudbreak.repository.environment.EnvironmentResourceRepository;
import com.sequenceiq.cloudbreak.repository.workspace.WorkspaceResourceRepository;
import com.sequenceiq.cloudbreak.service.AuthenticatedUserService;
import com.sequenceiq.cloudbreak.service.CloudbreakException;

@Component
public class PermissionCheckingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionCheckingUtils.class);

    @Inject
    private GrpcAuthorizationClient iamClient;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    public void checkPermissionByWorkspaceIdForUser(Long workspaceId, WorkspaceResource resource, ResourceAction action, User user) {
        // how can I define workspace?
        /*try {
            if (!iamClient.hasRight(authenticatedUserService.getUserCrn(), WorkspaceRightUtils.getRight(resource, action), resource.getShortName())) {
                throw new AccessDeniedException(format("You have no [%s] permission to %s.", action.name(), resource));
            }
        } catch (CloudbreakException e) {
            LOGGER.error(e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }*/
        try {
            iamClient.checkRight(authenticatedUserService.getUserCrn(), WorkspaceRightUtils.getRight(resource, action), resource.getShortName());
        } catch (CloudbreakException e) {
            LOGGER.error(e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }
    }

    public void checkPermissionsByTarget(Object target, User user, WorkspaceResource resource, ResourceAction action) {
        /*Iterable<?> iterableTarget = targetToIterable(target);
        Set<Long> workspaceIds = collectWorkspaceIds(iterableTarget);
        if (workspaceIds.isEmpty()) {
            return;
        }
        try {
            final String userCrn = authenticatedUserService.getUserCrn();
            List<Long> list = workspaceIds.stream()
                    .filter(workspaceId -> !iamClient.hasRight(userCrn, WorkspaceRightUtils.getRight(resource, action), resource.getShortName()))
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                list.stream().forEach(workspaceId -> LOGGER.error(format("You have no [%s] permission to %s.", action.name(), resource.getReadableName())));
                throw new AccessDeniedException(format("You have no [%s] permission to %s.", action.name(), resource.getReadableName()));
            }
        } catch (CloudbreakException e) {
            LOGGER.error(e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }*/

    }

    private Iterable<?> targetToIterable(Object target) {
        return target instanceof Iterable<?> ? (Iterable<?>) target : Collections.singletonList(target);
    }

    private Set<Long> collectWorkspaceIds(Iterable<?> target) {
        return StreamSupport.stream(target.spliterator(), false)
                .map(resource -> {
                    if (resource instanceof Optional && ((Optional<?>) resource).isPresent()) {
                        return (WorkspaceAwareResource) ((Optional<?>) resource).get();
                    } else if (resource instanceof WorkspaceAwareResource) {
                        return (WorkspaceAwareResource) resource;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .map(this::getWorkspaceId)
                .collect(Collectors.toSet());
    }

    private Long getWorkspaceId(WorkspaceAwareResource workspaceResource) {
        Workspace workspace = workspaceResource.getWorkspace();
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        Long workspaceId = workspace.getId();
        if (workspaceId == null) {
            throw new IllegalArgumentException("WorkspaceId cannot be null!");
        }
        return workspaceId;
    }

    Object checkPermissionsByPermissionSetAndProceed(WorkspaceResource resource, User user, Long workspaceId, ResourceAction action,
            ProceedingJoinPoint proceedingJoinPoint, MethodSignature methodSignature) {
        if (workspaceId == null) {
            throw new IllegalArgumentException("workspaceId cannot be null!");
        }
        checkPermissionByWorkspaceIdForUser(workspaceId, resource, action, user);
        return proceed(proceedingJoinPoint, methodSignature);
    }

    void validateIndex(int index, int length, String indexName) {
        if (index >= length) {
            throw new IllegalArgumentException(
                    format("The %s [%s] cannot be bigger than or equal to the methods argument count [%s]", indexName, index, length));
        }
    }

    public Object proceed(ProceedingJoinPoint proceedingJoinPoint, MethodSignature methodSignature) {
        try {
            Object proceed = proceedingJoinPoint.proceed();
            if (proceed == null) {
                LOGGER.debug("Return value is null, method signature: {}", methodSignature.toLongString());
            }
            return proceed;
        } catch (Throwable t) {
            throw new AccessDeniedException(t.getMessage(), t);
        }
    }

    Optional<Class<?>> getWorkspaceAwareRepositoryClass(ProceedingJoinPoint proceedingJoinPoint) {
        return Arrays.stream(proceedingJoinPoint.getTarget().getClass().getInterfaces())
                .filter(i -> {
                    List<Class<?>> interfaces = Arrays.asList(i.getInterfaces());
                    return interfaces.contains(WorkspaceResourceRepository.class) || interfaces.contains(EnvironmentResourceRepository.class);
                })
                .findFirst();
    }

    Optional<Annotation> getClassAnnotation(Class<?> repositoryClass) {
        return Arrays.stream(repositoryClass.getAnnotations())
                .filter(a -> a.annotationType().equals(WorkspaceResourceType.class))
                .findFirst();
    }
}
