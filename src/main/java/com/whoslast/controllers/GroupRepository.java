package com.whoslast.controllers;

import com.whoslast.entities.Party;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<Party, Long>{
}
