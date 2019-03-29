-- // CB-839 Connect Workspace AuthZ to IAM/UMS
-- Migration SQL that makes the change goes here.

CREATE TABLE user_workspace_permissions_bkp AS SELECT id, user_id, workspace_id, permissions FROM user_workspace_permissions;
CREATE TABLE workspace_users AS SELECT user_id as users_id, workspace_id as workspaces_id FROM user_workspace_permissions;
DROP TABLE user_workspace_permissions;
ALTER TABLE users DROP COLUMN tenant_permissions, DROP COLUMN cloudbreak_permissions;

-- //@UNDO
-- SQL to undo the change goes here.

CREATE TABLE user_workspace_permissions AS SELECT id, user_id, workspace_id, permissions FROM user_workspace_permissions_bkp;
DROP TABLE user_workspace_permissions_bkp;
ALTER TABLE users ADD COLUMN tenant_permissions TEXT, ADD COLUMN cloudbreak_permissions TEXT;
DROP TABLE workspace_users;