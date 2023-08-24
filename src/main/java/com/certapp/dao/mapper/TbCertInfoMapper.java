package com.certapp.dao.mapper;

import com.certapp.dao.entity.TbCertInfo;
import com.certapp.dao.entity.TbCertInfoExample;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface TbCertInfoMapper {

    @Select({ "select", "id, cert_config_id, cert_id, filed_info, cert_hash, tx_hash, cert_url, cert_state, publish_state, cert_pic", "from tb_cert_info", "where cert_config_id = #{certConfigId,jdbcType=INTEGER}" })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "cert_config_id", property = "certConfigId", jdbcType = JdbcType.INTEGER), @Result(column = "cert_id", property = "certId", jdbcType = JdbcType.VARCHAR), @Result(column = "filed_info", property = "filedInfo", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_hash", property = "certHash", jdbcType = JdbcType.VARCHAR), @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_url", property = "certUrl", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_state", property = "certState", jdbcType = JdbcType.INTEGER) })
    List<TbCertInfo> selectByCertConfigId(@Param("certConfigId") Integer certConfigId);

    @Delete({ "delete from tb_cert_info", "where cert_config_id = #{certConfigId,jdbcType=INTEGER}" })
    int deleteByCertConfigId(@Param("certConfigId") Integer certConfigId);

    @Update({ "update tb_cert_info", "set cert_pic = #{certPic,jdbcType=LONGVARCHAR}", "where id = #{id,jdbcType=INTEGER}" })
    int updateCertPicById(@Param("id") Integer id, @Param("certPic") String certPic);

    @Update({ "update tb_cert_info", "set cert_id = #{certId,jdbcType=VARCHAR}", "where id = #{id,jdbcType=INTEGER}" })
    int updateCertIdById(@Param("id") Integer id, @Param("certId") String certId);

    @Update({ "update tb_cert_info", "set cert_state = #{certState,jdbcType=INTEGER}", "where cert_config_id = #{certConfigId,jdbcType=INTEGER}" })
    int updateCertStateByConfigId(@Param("certConfigId") Integer certConfigI, @Param("certState") Integer certState);

    @Update({ "update tb_cert_info", "set tx_hash = #{txHash,jdbcType=VARCHAR},", "cert_time = #{certTime,jdbcType=TIMESTAMP},", "cert_state = #{certState,jdbcType=INTEGER}", "where id = #{id,jdbcType=INTEGER}" })
    int updateTxHashById(@Param("id") Integer id, @Param("txHash") String txHash, @Param("certState") Integer certState, @Param("certTime") Date certTime);

    @Select({ "select", TbCertInfoSqlProvider.ALL_COLUMN_FIELDS, "from tb_cert_info", "where LOWER(filed_info) like LOWER('%' ", "#{filedInfo,jdbcType=VARCHAR}", "'%') " })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "cert_config_id", property = "certConfigId", jdbcType = JdbcType.INTEGER), @Result(column = "cert_id", property = "certId", jdbcType = JdbcType.VARCHAR), @Result(column = "filed_info", property = "filedInfo", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_hash", property = "certHash", jdbcType = JdbcType.VARCHAR), @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_url", property = "certUrl", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_state", property = "certState", jdbcType = JdbcType.INTEGER), @Result(column = "publish_state", property = "publishState", jdbcType = JdbcType.INTEGER), @Result(column = "cert_time", property = "certTime", jdbcType = JdbcType.TIMESTAMP), @Result(column = "search_filed", property = "searchFiled", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_pic", property = "certPic", jdbcType = JdbcType.LONGVARCHAR) })
    List<TbCertInfo> selectByFuzzyFiledInfo(@Param("filedInfo") String filedInfo);

    @Select({ "select", "id, cert_config_id, cert_id, filed_info, cert_hash, tx_hash, cert_url, cert_state, ", "publish_state, cert_time, search_filed, cert_pic", "from tb_cert_info", "where search_filed = #{searchFiled,jdbcType=VARCHAR}" })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "cert_config_id", property = "certConfigId", jdbcType = JdbcType.INTEGER), @Result(column = "cert_id", property = "certId", jdbcType = JdbcType.VARCHAR), @Result(column = "filed_info", property = "filedInfo", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_hash", property = "certHash", jdbcType = JdbcType.VARCHAR), @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_url", property = "certUrl", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_state", property = "certState", jdbcType = JdbcType.INTEGER), @Result(column = "publish_state", property = "publishState", jdbcType = JdbcType.INTEGER), @Result(column = "cert_time", property = "certTime", jdbcType = JdbcType.TIMESTAMP), @Result(column = "search_filed", property = "searchFiled", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_pic", property = "certPic", jdbcType = JdbcType.LONGVARCHAR) })
    List<TbCertInfo> selectBySearchFiled(@Param("searchFiled") String searchFiled);

    @Update({ "update tb_cert_info", "set search_filed = #{searchFiled,jdbcType=LONGVARCHAR}", "where id = #{id,jdbcType=INTEGER}" })
    int updateSearchFiledById(@Param("id") Integer id, @Param("searchFiled") String searchFiled);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbCertInfoSqlProvider.class, method = "countByExample")
    long countByExample(TbCertInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @DeleteProvider(type = TbCertInfoSqlProvider.class, method = "deleteByExample")
    int deleteByExample(TbCertInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @Delete({ "delete from tb_cert_info", "where id = #{id,jdbcType=INTEGER}" })
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @Insert({ "insert into tb_cert_info (cert_config_id, cert_id, ", "filed_info, cert_hash, ", "tx_hash, cert_url, ", "cert_state, publish_state, ", "cert_time, search_filed, ", "cert_pic)", "values (#{certConfigId,jdbcType=INTEGER}, #{certId,jdbcType=VARCHAR}, ", "#{filedInfo,jdbcType=VARCHAR}, #{certHash,jdbcType=VARCHAR}, ", "#{txHash,jdbcType=VARCHAR}, #{certUrl,jdbcType=VARCHAR}, ", "#{certState,jdbcType=INTEGER}, #{publishState,jdbcType=INTEGER}, ", "#{certTime,jdbcType=TIMESTAMP}, #{searchFiled,jdbcType=VARCHAR}, ", "#{certPic,jdbcType=LONGVARCHAR})" })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    int insert(TbCertInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @InsertProvider(type = TbCertInfoSqlProvider.class, method = "insertSelective")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    int insertSelective(TbCertInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbCertInfoSqlProvider.class, method = "selectByExampleWithBLOBs")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "cert_config_id", property = "certConfigId", jdbcType = JdbcType.INTEGER), @Result(column = "cert_id", property = "certId", jdbcType = JdbcType.VARCHAR), @Result(column = "filed_info", property = "filedInfo", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_hash", property = "certHash", jdbcType = JdbcType.VARCHAR), @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_url", property = "certUrl", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_state", property = "certState", jdbcType = JdbcType.INTEGER), @Result(column = "publish_state", property = "publishState", jdbcType = JdbcType.INTEGER), @Result(column = "cert_time", property = "certTime", jdbcType = JdbcType.TIMESTAMP), @Result(column = "search_filed", property = "searchFiled", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_pic", property = "certPic", jdbcType = JdbcType.LONGVARCHAR) })
    List<TbCertInfo> selectByExampleWithBLOBs(TbCertInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbCertInfoSqlProvider.class, method = "selectByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "cert_config_id", property = "certConfigId", jdbcType = JdbcType.INTEGER), @Result(column = "cert_id", property = "certId", jdbcType = JdbcType.VARCHAR), @Result(column = "filed_info", property = "filedInfo", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_hash", property = "certHash", jdbcType = JdbcType.VARCHAR), @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_url", property = "certUrl", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_state", property = "certState", jdbcType = JdbcType.INTEGER), @Result(column = "publish_state", property = "publishState", jdbcType = JdbcType.INTEGER), @Result(column = "cert_time", property = "certTime", jdbcType = JdbcType.TIMESTAMP), @Result(column = "search_filed", property = "searchFiled", jdbcType = JdbcType.VARCHAR) })
    List<TbCertInfo> selectByExample(TbCertInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @Select({ "select", "id, cert_config_id, cert_id, filed_info, cert_hash, tx_hash, cert_url, cert_state, ", "publish_state, cert_time, search_filed, cert_pic", "from tb_cert_info", "where id = #{id,jdbcType=INTEGER}" })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "cert_config_id", property = "certConfigId", jdbcType = JdbcType.INTEGER), @Result(column = "cert_id", property = "certId", jdbcType = JdbcType.VARCHAR), @Result(column = "filed_info", property = "filedInfo", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_hash", property = "certHash", jdbcType = JdbcType.VARCHAR), @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_url", property = "certUrl", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_state", property = "certState", jdbcType = JdbcType.INTEGER), @Result(column = "publish_state", property = "publishState", jdbcType = JdbcType.INTEGER), @Result(column = "cert_time", property = "certTime", jdbcType = JdbcType.TIMESTAMP), @Result(column = "search_filed", property = "searchFiled", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_pic", property = "certPic", jdbcType = JdbcType.LONGVARCHAR) })
    TbCertInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbCertInfoSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") TbCertInfo record, @Param("example") TbCertInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbCertInfoSqlProvider.class, method = "updateByExampleWithBLOBs")
    int updateByExampleWithBLOBs(@Param("record") TbCertInfo record, @Param("example") TbCertInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbCertInfoSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") TbCertInfo record, @Param("example") TbCertInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbCertInfoSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(TbCertInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @Update({ "update tb_cert_info", "set cert_config_id = #{certConfigId,jdbcType=INTEGER},", "cert_id = #{certId,jdbcType=VARCHAR},", "filed_info = #{filedInfo,jdbcType=VARCHAR},", "cert_hash = #{certHash,jdbcType=VARCHAR},", "tx_hash = #{txHash,jdbcType=VARCHAR},", "cert_url = #{certUrl,jdbcType=VARCHAR},", "cert_state = #{certState,jdbcType=INTEGER},", "publish_state = #{publishState,jdbcType=INTEGER},", "cert_time = #{certTime,jdbcType=TIMESTAMP},", "search_filed = #{searchFiled,jdbcType=VARCHAR},", "cert_pic = #{certPic,jdbcType=LONGVARCHAR}", "where id = #{id,jdbcType=INTEGER}" })
    int updateByPrimaryKeyWithBLOBs(TbCertInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @Update({ "update tb_cert_info", "set cert_config_id = #{certConfigId,jdbcType=INTEGER},", "cert_id = #{certId,jdbcType=VARCHAR},", "filed_info = #{filedInfo,jdbcType=VARCHAR},", "cert_hash = #{certHash,jdbcType=VARCHAR},", "tx_hash = #{txHash,jdbcType=VARCHAR},", "cert_url = #{certUrl,jdbcType=VARCHAR},", "cert_state = #{certState,jdbcType=INTEGER},", "publish_state = #{publishState,jdbcType=INTEGER},", "cert_time = #{certTime,jdbcType=TIMESTAMP},", "search_filed = #{searchFiled,jdbcType=VARCHAR}", "where id = #{id,jdbcType=INTEGER}" })
    int updateByPrimaryKey(TbCertInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert({ "<script>", "insert into tb_cert_info (cert_config_id, ", "cert_id, filed_info, ", "cert_hash, tx_hash, ", "cert_url, cert_state, ", "publish_state, cert_time, ", "search_filed, cert_pic)", "values<foreach collection=\"list\" item=\"detail\" index=\"index\" separator=\",\">(#{detail.certConfigId,jdbcType=INTEGER}, ", "#{detail.certId,jdbcType=VARCHAR}, #{detail.filedInfo,jdbcType=VARCHAR}, ", "#{detail.certHash,jdbcType=VARCHAR}, #{detail.txHash,jdbcType=VARCHAR}, ", "#{detail.certUrl,jdbcType=VARCHAR}, #{detail.certState,jdbcType=INTEGER}, ", "#{detail.publishState,jdbcType=INTEGER}, #{detail.certTime,jdbcType=TIMESTAMP}, ", "#{detail.searchFiled,jdbcType=VARCHAR}, #{detail.certPic,jdbcType=LONGVARCHAR})</foreach></script>" })
    int batchInsert(List<TbCertInfo> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_cert_info
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbCertInfoSqlProvider.class, method = "getOneByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "cert_config_id", property = "certConfigId", jdbcType = JdbcType.INTEGER), @Result(column = "cert_id", property = "certId", jdbcType = JdbcType.VARCHAR), @Result(column = "filed_info", property = "filedInfo", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_hash", property = "certHash", jdbcType = JdbcType.VARCHAR), @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_url", property = "certUrl", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_state", property = "certState", jdbcType = JdbcType.INTEGER), @Result(column = "publish_state", property = "publishState", jdbcType = JdbcType.INTEGER), @Result(column = "cert_time", property = "certTime", jdbcType = JdbcType.TIMESTAMP), @Result(column = "search_filed", property = "searchFiled", jdbcType = JdbcType.VARCHAR), @Result(column = "cert_pic", property = "certPic", jdbcType = JdbcType.LONGVARCHAR) })
    Optional<TbCertInfo> getOneByExample(TbCertInfoExample example);
}
