package com.timzaak.action

import com.timzaak.dao.AccessDao
import com.timzaak.entity.User
import very.util.security.{ Access, AccessDenied, Permission, PermissionCheckable }
import scala.concurrent.Future

trait AccessAction extends Action {
  //redis...
  protected def accessDao: AccessDao

  def withUserAccess(userId: UserId,
                     permissionDesc: PermissionCheckable,
                     access: Access): Future[Unit] = {
    accessDao
      .getUserPermission(permissionDesc.resource, userId)
      .map(_.getOrElse(Permission.Nothing))
      .map { permission =>
        if (!(permission ? access)) {
          throw AccessDenied(permissionDesc.resource, access)
        }
      }
  }

  def withGroupAccess(groupId: GroupId,
                      permissionDesc: PermissionCheckable,
                      access: Access): Future[Unit] = {
    accessDao
      .getGroupPermission(permissionDesc.resource, groupId)
      .map(_.getOrElse(Permission.Nothing))
      .map { permission =>
        if (!(permission ? access)) {
          throw AccessDenied(permissionDesc.resource, access)
        }
      }
  }

  def withGroupsAccess(groupIds: Seq[GroupId],
                       permissionDesc: PermissionCheckable,
                       access: Access): Future[Unit] = {
    accessDao
      .getGroupsPermission(permissionDesc.resource, groupIds)
      .map { permissions =>
        if (!(Permission.union(permissions: _*) ? access)) {
          throw AccessDenied(permissionDesc.resource, access)
        }
      }
  }

  def withAccess(user: User, permissionDesc: PermissionCheckable, access: Access): Future[Unit] =
    withGroupsAccess(user.groupIds, permissionDesc, access).recoverWith {
      case _ => withUserAccess(user.id, permissionDesc, access)
    }
}
