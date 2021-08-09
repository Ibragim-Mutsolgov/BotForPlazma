package com.tsecho.bots.repository.bill;

import com.tsecho.bots.model.bill.ClientService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConfigurationProperties("sql")
public interface ClientServiceRepository extends JpaRepository<ClientService,Long>{
    @Query(value="select * from ActiveService where mobile = ?1 or phone = ?2", nativeQuery=true)
    List<ClientService> findAllUserByMobileOrPhone(String mobile, String phone);
}
