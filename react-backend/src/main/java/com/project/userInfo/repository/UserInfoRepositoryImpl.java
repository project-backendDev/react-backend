package com.project.userInfo.repository;

import static com.project.userInfo.model.QUserInfo.userInfo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.project.cmm.SearchRequestDto;
import com.project.userInfo.model.UserInfo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class UserInfoRepositoryImpl implements UserInfoRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public Page<UserInfo> getUserList(SearchRequestDto searchDto, Pageable pageable) {
        
		List<UserInfo> content = queryFactory
                .selectFrom(userInfo)
                .where(searchCondition(searchDto.getSearchType(), searchDto.getKeyword()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(userInfo.regDate.desc()) 
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(userInfo.count())
                .from(userInfo)
                .where(searchCondition(searchDto.getSearchType(), searchDto.getKeyword()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression searchCondition(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        if ("userId".equals(searchType)) {
            return userInfo.userId.contains(keyword);
        } else if ("userName".equals(searchType)) {
            return userInfo.userNm.contains(keyword);
        } else if ("userEmail".equals(searchType)) {
            return userInfo.userEmail.contains(keyword);
        }
        
        return null;
    }

}
