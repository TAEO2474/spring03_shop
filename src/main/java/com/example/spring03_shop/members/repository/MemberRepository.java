package com.example.spring03_shop.members.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.spring03_shop.member.entity.MembersEntity;

@Repository
public interface MemberRepository extends JpaRepository<MembersEntity, String> {

}
