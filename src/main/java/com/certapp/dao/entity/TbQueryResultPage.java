package com.certapp.dao.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class TbQueryResultPage implements Serializable {

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_query_result_page.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_query_result_page.cert_config_id
     *
     * @mbg.generated
     */
    private Integer certConfigId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_query_result_page.title
     *
     * @mbg.generated
     */
    private String title;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_query_result_page.enable_fields
     *
     * @mbg.generated
     */
    private String enableFields;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_query_result_page.explain_doc
     *
     * @mbg.generated
     */
    private String explainDoc;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_query_result_page.bottom_log
     *
     * @mbg.generated
     */
    private String bottomLog;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tb_query_result_page
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;
}
