package com.example.pasir_maderak_michal.service;

import com.example.pasir_maderak_michal.dto.GroupDto;
import com.example.pasir_maderak_michal.model.Group;
import com.example.pasir_maderak_michal.model.Membership;
import com.example.pasir_maderak_michal.model.User;
import com.example.pasir_maderak_michal.repository.DebtRepository;
import com.example.pasir_maderak_michal.repository.GroupRepository;
import com.example.pasir_maderak_michal.repository.MembershipRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipService membershipService;
    private final DebtRepository debtRepository;
    public GroupService(GroupRepository groupRepository,
                        MembershipRepository membershipRepository,
                        MembershipService membershipService,
                        DebtRepository debtRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.membershipService = membershipService;
        this.debtRepository = debtRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group createGroup(GroupDto groupDTO) {
        User owner = membershipService.getCurrentUser();
        Group group = new Group();
        group.setName(groupDTO.getName());
        group.setOwner(owner);
        Group savedGroup = groupRepository.save(group);

        Membership membership = new Membership();
        membership.setUser(owner);
        membership.setGroup(savedGroup);
        membershipRepository.save(membership);

        return savedGroup;
    }

    public void deleteGroup(long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group o ID " + id + " nie istnieje."));

        debtRepository.deleteAll(debtRepository.findByGroupId(id));
        membershipRepository.deleteAll(membershipRepository.findByGroupId(id));

        groupRepository.delete(group);
    }
}