package com.sequenceiq.cloudbreak.service.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sequenceiq.cloudbreak.TestUtil;
import com.sequenceiq.cloudbreak.api.endpoint.v4.workspace.requests.ChangeWorkspaceUsersV4Request;
import com.sequenceiq.cloudbreak.domain.workspace.Tenant;
import com.sequenceiq.cloudbreak.domain.workspace.User;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.repository.workspace.WorkspaceRepository;
import com.sequenceiq.cloudbreak.service.TransactionService;
import com.sequenceiq.cloudbreak.service.TransactionService.TransactionExecutionException;
import com.sequenceiq.cloudbreak.service.user.UserService;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceServiceTest {

    private static final String WORKSPACE_NAME = "test-workspace";

    private static final String TENANT_NAME = "test-tenant";

    private static final String USER_ID_2 = "user2";

    private static final String USER_ID_3 = "user3";

    @Spy
    private final TransactionService transactionService = new TransactionService();

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private UserService userService;

    @Mock
    private WorkspaceModificationVerifierService verifierService;

    @InjectMocks
    private final WorkspaceService underTest = new WorkspaceService();

    private final Tenant testTenant = new Tenant();

    private final Workspace testWorkspace = new Workspace();

    private final User initiator = new User();

    @Before
    public void setup() throws TransactionExecutionException {
        initiator.setId(1L);
        initiator.setUserId("initiator");
        initiator.setTenant(testTenant);
        testTenant.setId(1L);
        testTenant.setName(TENANT_NAME);
        testWorkspace.setName(WORKSPACE_NAME);
        testWorkspace.setId(1L);
        testWorkspace.setTenant(testTenant);
        doAnswer(invocation -> ((Supplier<?>) invocation.getArgument(0)).get()).when(transactionService).required(any());
        when(workspaceRepository.getByName(WORKSPACE_NAME, testTenant)).thenReturn(testWorkspace);
    }

    @Test
    public void testRemoveUsers() {
        Set<String> userIds = new HashSet<>();
        userIds.add(USER_ID_2);
        userIds.add(USER_ID_3);

        User user2 = TestUtil.user(2L, USER_ID_2);
        User user3 = TestUtil.user(3L, USER_ID_3);
        Set<User> users = Set.of(user2, user3);

        when(userService.getByUsersIds(userIds)).thenReturn(users);

        Set<User> result = underTest.removeUsers(WORKSPACE_NAME, userIds, initiator);

        assertEquals(2L, result.size());
        assertTrue(result.contains(user2));
        assertTrue(result.contains(user3));
    }

    @Test
    public void testAddUsers() {
        User user2 = TestUtil.user(2L, USER_ID_2);
        when(userService.getByUserId(USER_ID_2)).thenReturn(user2);
        User user3 = TestUtil.user(3L, USER_ID_3);
        when(userService.getByUserId(USER_ID_3)).thenReturn(user3);
        Set<ChangeWorkspaceUsersV4Request> changeWorkspaceUsersV4Requests = changeWorkspaceUsersJsons();

        Set<User> result = underTest.addUsers(WORKSPACE_NAME, changeWorkspaceUsersV4Requests, initiator);

        assertEquals(2L, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(USER_ID_2)));
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(USER_ID_3)));
    }

    @Test
    public void testUpdateUsers() {
        User user2 = TestUtil.user(2L, USER_ID_2);
        when(userService.getByUserId(USER_ID_2)).thenReturn(user2);
        User user3 = TestUtil.user(3L, USER_ID_3);
        when(userService.getByUserId(USER_ID_3)).thenReturn(user3);
        Set<ChangeWorkspaceUsersV4Request> changeWorkspaceUsersV4Requests = changeWorkspaceUsersJsons();

        Set<User> result = underTest.updateUsers(WORKSPACE_NAME, changeWorkspaceUsersV4Requests, initiator);

        assertEquals(2L, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(USER_ID_2)));
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(USER_ID_3)));
    }

    @Test
    public void testChangeUsers() {
        User user2 = TestUtil.user(2L, USER_ID_2);
        User user3 = TestUtil.user(3L, USER_ID_3);

        when(userService.getByUsersIds(anySet())).thenReturn(Set.of(user2, user3));

        Set<ChangeWorkspaceUsersV4Request> changeWorkspaceUsersV4Requests = changeWorkspaceUsersJsons();

        Set<User> result = underTest.changeUsers(WORKSPACE_NAME, changeWorkspaceUsersV4Requests, initiator);

        assertEquals(2L, result.size());

    }

    private Set<ChangeWorkspaceUsersV4Request> changeWorkspaceUsersJsons() {
        ChangeWorkspaceUsersV4Request json1 = TestUtil.changeWorkspaceUsersJson(USER_ID_2);
        ChangeWorkspaceUsersV4Request json2 = TestUtil.changeWorkspaceUsersJson(USER_ID_3);
        return Set.of(json1, json2);
    }
}