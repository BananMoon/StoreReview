package com.review.storereview.repository.cms;

import com.review.storereview.dao.cms.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Class       : BaseCommentRepository
 * Author      : 조 준 희
 * Description : Class Description
 * History     : [2022-01-24] - 조 준희 - Class Create
 */
@Repository
public interface BaseCommentRepository extends JpaRepository<Comment, Long> {
//    @Query(value=   "SELECT CO.ID, CO.REVIEW, CO.SUID, CO.SAID, CO.CONTENT, CO.CREATED_AT, CO.UPDATED_AT, UI.USER_ID" +
//                    "  FROM COMMENT CO" +
//                    "  LEFT OUTER JOIN CUST_DB.USER_INFO UI" +
//                    "    ON CO.SUID = UI.SUID " +
//                    "   AND CO.SAID = UI.SAID    " +
//                    " WHERE CO.REVIEW = ?1 "
//            , countQuery = "SELECT COUNT(1)" +
//                            " FROM COMMENT CO" +
//                            " LEFT OUTER JOIN CUST_DB.USER_INFO UI" +
//                            "   ON CO.SUID = UI.SUID " +
//                            "  AND CO.SAID = UI.SAID    " +
//                            "WHERE CO.REVIEW = ?1 ", nativeQuery = true )
    Page<Comment> findAllByReviewIdAndIsDelete(Long reviewId, Integer IsDelete, Pageable pageRequest);
    List<Comment> findAllByReviewIdAndIsDelete(Long reviewId, Integer IsDelete);
    Comment findByCommentId(Long commentID);

    @Query(value= "SELECT COUNT(COMMENT.COMMENT_ID) from COMMENT where COMMENT.REVIEW_ID =?", nativeQuery = true)
    int findCommentNumByReviewId(Long reviewId);
}
