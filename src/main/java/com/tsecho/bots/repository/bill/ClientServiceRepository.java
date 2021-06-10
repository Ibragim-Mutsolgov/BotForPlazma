package com.tsecho.bots.repository.bill;

import com.tsecho.bots.model.bill.ClientService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Repository
@Qualifier("dsBill")
public interface ClientServiceRepository extends JpaRepository<ClientService,Long>{
    @Query(value="select `billing`.`vgroups`.`vg_id`      AS `id`,\n" +
            "       `billing`.`accounts`.`name`      AS `name`,\n" +
            "       `billing`.`agreements`.`number`  AS `number`,\n" +
            "       `billing`.`agreements`.`balance` AS `balance`,\n" +
            "       `billing`.`tarifs`.`descr`       AS `descr`,\n" +
            "       `billing`.`vgroups`.`blocked`    AS `blocked`,\n" +
            "       `billing`.`accounts`.`login`     AS `login`,\n" +
            "       `billing`.`accounts`.`pass`      AS `pass`,\n" +
            "       `billing`.`accounts`.`mobile`    AS `mobile`,\n" +
            "       `billing`.`accounts`.`phone`     AS `phone`\n" +
            "from ((`billing`.`vgroups` left join (`billing`.`agreements` left join `billing`.`accounts` on ((`billing`.`accounts`.`uid` = `billing`.`agreements`.`uid`))) on ((`billing`.`agreements`.`agrm_id` = `billing`.`vgroups`.`agrm_id`)))\n" +
            "         join `billing`.`tarifs` on ((`billing`.`vgroups`.`tar_id` = `billing`.`tarifs`.`tar_id`)))\n" +
            "where ((`billing`.`accounts`.`archive` = 0) and (`billing`.`accounts`.`type` = 2) and\n" +
            "       (`billing`.`agreements`.`archive` = 0) and (`billing`.`vgroups`.`archive` = 0) and\n" +
            "       (`billing`.`vgroups`.`id` = 1)) and `billing`.`accounts`.`mobile` = ?1 and `billing`.`accounts`.`phone` = ?2", nativeQuery=true)
    List<ClientService> findAllUserByMobileOrPhone(String mobile, String phone);
}
