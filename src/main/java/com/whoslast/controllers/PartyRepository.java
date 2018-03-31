package com.whoslast.controllers;

import com.whoslast.entities.Party;
import com.whoslast.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends CrudRepository<Party, Long>{

    @Query(value = "SELECT * FROM party WHERE name=?1", nativeQuery = true)
    Party findGroupByName(String name);

    @Query(value = "SELECT EXISTS(SELECT * FROM party WHERE party_id=?1)", nativeQuery = true)
    Integer checkExistenceOfParty(Integer partyId);
}
