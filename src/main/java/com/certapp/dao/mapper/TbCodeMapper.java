package com.certapp.dao.mapper;

import com.certapp.dao.entity.TbCode;
import com.certapp.dao.entity.TbCodeExample;
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

public interface TbCodeMapper {

    @Select({ "select", TbCodeSqlProvider.ALL_COLUMN_FIELDS, "from tb_code", "where LOWER(code) like LOWER('%' ", "#{code,jdbcType=VARCHAR}", "'%') " })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR) })
    TbCode selectByCode(@Param("code") String code);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbCodeSqlProvider.class, method = "countByExample")
    long countByExample(TbCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @DeleteProvider(type = TbCodeSqlProvider.class, method = "deleteByExample")
    int deleteByExample(TbCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @Delete({ "delete from tb_code", "where id = #{id,jdbcType=INTEGER}" })
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @Insert({ "insert into tb_code (code)", "values (#{code,jdbcType=VARCHAR})" })
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    int insert(TbCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @InsertProvider(type = TbCodeSqlProvider.class, method = "insertSelective")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    int insertSelective(TbCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbCodeSqlProvider.class, method = "selectByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR) })
    List<TbCode> selectByExample(TbCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @Select({ "select", "id, code", "from tb_code", "where id = #{id,jdbcType=INTEGER}" })
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR) })
    TbCode selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbCodeSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") TbCode record, @Param("example") TbCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbCodeSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") TbCode record, @Param("example") TbCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbCodeSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(TbCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @Update({ "update tb_code", "set code = #{code,jdbcType=VARCHAR}", "where id = #{id,jdbcType=INTEGER}" })
    int updateByPrimaryKey(TbCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert({ "<script>", "insert into tb_code (code)", "values<foreach collection=\"list\" item=\"detail\" index=\"index\" separator=\",\">(#{detail.code,jdbcType=VARCHAR})</foreach></script>" })
    int batchInsert(List<TbCode> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_code
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbCodeSqlProvider.class, method = "getOneByExample")
    @Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true), @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR) })
    Optional<TbCode> getOneByExample(TbCodeExample example);
}
