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
		// 1. 리스트 조회
        List<UserInfo> content = queryFactory
                // ★ [핵심 해결 3] select(userInfo).from(...) 대신 selectFrom(...) 사용
                // 이렇게 하면 '1번 오류'가 해결됩니다.
                .selectFrom(userInfo)
                .where(searchCondition(searchDto.getSearchType(), searchDto.getKeyword()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                // '2번 오류' 해결: 위에서 java.util.Date를 import 했으므로 이제 인식됨
                .orderBy(userInfo.regDate.desc()) 
                .fetch();

        // 2. 카운트 조회
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

        // '3번 오류' 해결: 관련 import가 다 들어갔으므로 StringPath 오류 사라짐
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
