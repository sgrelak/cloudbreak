package com.sequenceiq.cloudbreak.authorization;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import com.sequenceiq.cloudbreak.TestUtil;
import com.sequenceiq.cloudbreak.domain.workspace.User;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.domain.workspace.WorkspaceAwareResource;

@RunWith(Parameterized.class)
public class PermissionCheckingUtilsBulkTest {

    private static final Long WORKSPACE_ID = 1L;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private PermissionCheckingUtils underTest;

    @Mock
    private User user;

    @Mock
    private WorkspaceAwareResource workspaceAwareResource;

    @Mock
    private Workspace workspace;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private MethodSignature methodSignature;

    private ResourceAction action;

    private WorkspaceResource resource;

    public PermissionCheckingUtilsBulkTest(ResourceAction action, WorkspaceResource resource) {
        this.action = action;
        this.resource = resource;
    }

    @Parameters(name = "Current Action - WorkspaceResource pair: [{0} - {1}]")
    public static Object[][] data() {
        return TestUtil.combinationOf(ResourceAction.values(), WorkspaceResource.values());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(workspaceAwareResource.getWorkspace()).thenReturn(workspace);
    }

    @Test
    public void testCheckPermissionByWorkspaceIdForUserWhenUserWorkspacePermissionsIsNullThenAccessDeniedExceptionComes() {

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(format("You have no [%s] permission to %s.", action.name(), resource));

        underTest.checkPermissionByWorkspaceIdForUser(WORKSPACE_ID, resource, action, user);
    }

    @Test
    public void testCheckPermissionByWorkspaceIdForUserWhenHasNoPermissionThenAccessDeniedExceptionComes() {
        Set<String> permissionSet = Collections.emptySet();

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(format("You have no [%s] permission to %s.", action.name(), resource));

        underTest.checkPermissionByWorkspaceIdForUser(WORKSPACE_ID, resource, action, user);

    }

    @Test
    public void testCheckPermissionByWorkspaceIdForUserWhenHasPermissionThenNoExceptionComes() {
        Set<String> permissionSet = Collections.emptySet();

        underTest.checkPermissionByWorkspaceIdForUser(WORKSPACE_ID, resource, action, user);

    }

    @Test
    public void testCheckPermissionsByTargetWhenWorkspaceIdsAreEmptyThenNothingSpecialHappens() {
        Object o = new Object();

        underTest.checkPermissionsByTarget(o, user, resource, action);

    }

    @Test
    public void testCheckPermissionsByTargetWhenWorkspaceIdsAreNotEmptyButItsSizeNotEqualsToTheUserWorkspacePermissionsThenAccessDeniedExceptionComes() {
        when(workspace.getId()).thenReturn(WORKSPACE_ID);

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(format("You have no [%s] permission to %s.", action.name(), resource.getReadableName()));

        underTest.checkPermissionsByTarget(workspaceAwareResource, user, resource, action);

    }

    @Test
    public void testCheckPermissionsByTargetWhenUserWorkspacePermissionsSetIsEmptyAndWorkspacePermissionUtilTellsItIsFineThenNoExceptionComes() {
        when(workspace.getId()).thenReturn(WORKSPACE_ID);

        underTest.checkPermissionsByTarget(workspaceAwareResource, user, resource, action);
    }

    @Test
    public void testCheckPermissionsByTargetWhenUserWorkspacePermissionsSetIsEmptyAndWorkspacePermissionUtilThinksItIsNotFineThenAccessDeniedExceptionComes() {
        when(workspace.getId()).thenReturn(WORKSPACE_ID);

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(format("You have no [%s] permission to %s.", action.name(), resource.getReadableName()));

        underTest.checkPermissionsByTarget(workspaceAwareResource, user, resource, action);
    }

    @Test
    public void testCheckPermissionsByPermissionSetAndProceedWhenWorkspaceIdIsNUllThenIllegalArgumentExceptionComes() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("workspaceId cannot be null!");

        underTest.checkPermissionsByPermissionSetAndProceed(resource, user, null, action, proceedingJoinPoint, methodSignature);
    }

    @Test
    public void testCheckPermissionsByPermissionSetAndProceedWhenUserWorkspacePermissionsIsNUllThenAccessDeniedExceptionComes() {

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(format("You have no [%s] permission to %s.", action.name(), resource.getReadableName()));

        underTest.checkPermissionsByPermissionSetAndProceed(resource, user, WORKSPACE_ID, action, proceedingJoinPoint, methodSignature);
    }

    @Test
    //CHECKSTYLE:OFF
    public void testCheckPermissionsByPermissionSetAndProceedWhenProceedingJoinPointProceedOperationThrowsExceptionThenAccessDeniedExceptionComes()
            throws Throwable {
        //CHECKSTYLE:ON
        String someMessage = "hereComesTheSanta";
        doThrow(new RuntimeException(someMessage)).when(proceedingJoinPoint).proceed();

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(someMessage);

        underTest.checkPermissionsByPermissionSetAndProceed(resource, user, WORKSPACE_ID, action, proceedingJoinPoint, methodSignature);
    }

    @Test
    public void testCheckPermissionsByPermissionSetAndProceedWhenWorkspacePermissionUtilTellsItHasNoPermissionThenAccessDeniedExceptionComes() {

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(format("You have no [%s] permission to %s.", action.name(), resource.getReadableName()));

        underTest.checkPermissionsByPermissionSetAndProceed(resource, user, WORKSPACE_ID, action, proceedingJoinPoint, methodSignature);
    }

    @Test
    //CHECKSTYLE:OFF
    public void testCheckPermissionsByPermissionSetAndProceedWhenProceedingJoinPointProceedReturnsNullThenNullReturns() throws Throwable {
        //CHECKSTYLE:ON
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        Object result = underTest.checkPermissionsByPermissionSetAndProceed(resource, user, WORKSPACE_ID, action, proceedingJoinPoint, methodSignature);

        assertNull(result);
    }

    @Test
    //CHECKSTYLE:OFF
    public void testCheckPermissionsByPermissionSetAndProceedWhenProceedingJoinPointProceedReturnsAnObjectThenThatObjectReturnsAtTheEnd() throws Throwable {
        //CHECKSTYLE:ON
        Object expected = new Object();
        when(proceedingJoinPoint.proceed()).thenReturn(expected);

        Object result = underTest.checkPermissionsByPermissionSetAndProceed(resource, user, WORKSPACE_ID, action, proceedingJoinPoint, methodSignature);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testCheckPermissionByPermissionSetWhenWorkspacePermissionUtilTellsHasNoPermissionThenAccessDeniedExceptionComesRegardlessOfTheGivenTypes() {

        thrown.expect(AccessDeniedException.class);
        thrown.expectMessage(format("You have no [%s] permission to %s.", action.name(), resource.getReadableName()));
    }

    @Test
    public void testCheckPermissionByPermissionSetWhenWorkspacePermissionUtilTellsHasPermissionThenNoExceptionComes() {
    }

}