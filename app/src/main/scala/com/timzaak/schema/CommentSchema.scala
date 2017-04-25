package com.timzaak.schema

import com.timzaak.action.CommentAction
import com.timzaak.entity.Comment
import sangria.schema._
import sangria.macros.derive._
import very.util.graphql.CommonSchema

trait CommentSchema extends CommonSchema {
  //need it for macro
  import very.util.graphql.DateTimeSchema._

  private implicit val commentObjectType = deriveObjectType[GraphQLContext, Comment](
    ObjectTypeName("Comment"),
    ObjectTypeDescription("the sample comment version")
  )

  private implicit val CommentPageObjectType = pageType[GraphQLContext, Comment]("comments", commentObjectType)

  //private implicit val commentObjectsType = ListType(commentObjectType)
  private val commentQueryType: ObjectType[GraphQLContext, CommentAction] =
    deriveObjectType[GraphQLContext, CommentAction](
      ObjectTypeName("commentQueryAction"),
      IncludeMethods(),
      AddFields(Field(
        name = "myComments",
        description = "get my comments",
        arguments = pageArg :: pageSizeArg :: Nil,
        fieldType = CommentPageObjectType,
        resolve = c => c.ctx.di.commentAction.myComments(c.ctx.userId, c arg pageSizeArg, c arg pageArg)
      ))
    )


  val commentSchemaQuery = fields[GraphQLContext, Unit](
    Field(
      name = "comment",
      fieldType = commentQueryType,
      resolve = c => c.ctx.di.commentAction: CommentAction
    )
  )

  private implicit val commentObjectInputType = deriveInputObjectType[Comment](
    InputObjectTypeName("CommentArg"),
    ExcludeInputFields("time", "id")
  )


  private val toId = Argument("toId", LongType)
  private val content = Argument("content", StringType)

  private val commentMutationType = deriveObjectType[GraphQLContext, CommentAction](
    ObjectTypeName("commentMutationAction"),
    IncludeMethods(),
    AddFields(Field(
      name = "postComment",
      description = "post comment",
      arguments = toId :: content :: Nil,
      fieldType = LongType,
      resolve = c => c.ctx.di.commentAction.postComment(c.ctx.userId, c arg toId, c arg content)
    ))
  )

  val commentSchemaMutation = fields[GraphQLContext, Unit](
    Field(
      name = "comment",
      fieldType = commentMutationType,
      resolve = c => c.ctx.di.commentAction: CommentAction
    )
  )


}