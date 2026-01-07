package com.superhealthclaim.claimservice.repository;

import com.superhealthclaim.claimservice.entity.Claim;
import com.superhealthclaim.claimservice.entity.ClaimStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
	List<Claim> findByStatusNot(ClaimStatus status);

}
