package com.ss.scrumptious_orders.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ss.scrumptious.common_entities.entity.Menuitem;

@Repository
public interface MenuitemRepository extends JpaRepository<Menuitem, Long> {

}