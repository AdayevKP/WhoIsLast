package com.whoslast.controllers;

import com.whoslast.entities.Invite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends CrudRepository<Invite, Long>{
}
