package com.sunic.community.aggregate.community.store;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import com.sunic.community.aggregate.community.store.repository.CommunityRepository;
import com.sunic.community.spec.community.entity.Community;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommunityStore {

	private final CommunityRepository communityRepository;

	public Community save(Community community) {
		CommunityJpo communityJpo = communityRepository.save(CommunityJpo.fromDomain(community));
		return communityJpo.toDomain();
	}

	public Community findById(Integer id) {
		CommunityJpo communityJpo = communityRepository.findById(id)
			.orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + id));
		return communityJpo.toDomain();
	}

	public List<Community> findAll() {
		return communityRepository.findAll().stream()
			.map(CommunityJpo::toDomain).collect(Collectors.toList());
	}

	public Community update(Community community) {
		CommunityJpo communityJpo = communityRepository.findById(community.getId())
			.orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + community.getId()));
		communityJpo.update(community);

		return communityRepository.save(communityJpo).toDomain();
	}

	public void deleteById(Integer id) {
		if (!communityRepository.existsById(id)) {
			throw new CommunityNotFoundException("Community not found with id: " + id);
		}
		communityRepository.deleteById(id);
	}
}