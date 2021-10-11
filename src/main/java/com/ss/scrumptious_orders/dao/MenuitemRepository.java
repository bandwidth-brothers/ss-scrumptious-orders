package com.ss.scrumptious_orders.dao;

import com.ss.scrumptious_orders.entity.Menuitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuitemRepository extends JpaRepository<Menuitem, Long> {

}