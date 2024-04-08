package com.t3q.dranswer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_api_log", schema = "swint", catalog = "svc_plf")
public class LoggingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long seq_id;
    String req_id;
    String req_user;
    String req_md;
    String req_uri;
    String req_prm;
    String req_body;
    LocalDateTime req_dt;

    String res_user;
    String res_msg;
    int res_status;
    String res_body;
    LocalDateTime res_dt;

}
