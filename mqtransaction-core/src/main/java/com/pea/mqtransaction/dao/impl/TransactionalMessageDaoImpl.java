package com.pea.mqtransaction.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.pea.mqtransaction.dao.TransactionalMessageDO;
import com.pea.mqtransaction.dao.TransactionalMessageDao;

/**
 * @author caojingui
 */
public class TransactionalMessageDaoImpl implements TransactionalMessageDao {

    private static final String INSERT_SQL =
            "INSERT INTO transactional_message(exchange_name, routing_key, business_key, message_type, "
                    + "message_body, message_header,  create_time,  update_time) "
                    + "VALUES (?,?,?,?,?,?,?, ?)";
    private static final String SELECT_ID_SQL = "SELECT id, exchange_name, routing_key, business_key, message_type, "
            + "message_body, message_header, create_time, update_time "
            + "FROM  transactional_message WHERE id=?";
    private static final String SELECT_SQL = "SELECT id, exchange_name, routing_key, business_key, message_type, "
            + "message_body, message_header, create_time, update_time "
            + "FROM  transactional_message "
            + "WHERE message_type = ? AND create_time<=? ORDER BY id LIMIT 1000 ";
    private static final String DELETE_SQL = "DELETE FROM transactional_message WHERE id=?";

    private final JdbcTemplate jdbcTemplate;

    public TransactionalMessageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long insert(final TransactionalMessageDO message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, message.getExchangeName());
                ps.setString(2, message.getRoutingKey());
                ps.setString(3, message.getBusinessKey());
                ps.setString(4, message.getMessageType());
                ps.setString(5, message.getMessageBody());
                ps.setString(6, message.getMessageHeader());
                ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                return ps;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        message.setId(id);
        return id;
    }

    @Override
    public int delete(final Long messageId) {
        return jdbcTemplate.update(DELETE_SQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, messageId);
            }
        });
    }

    @Override
    public List<TransactionalMessageDO> queryNeedResend(String messageType, long time) {
        Date createTime = new Date(System.currentTimeMillis() - time);
        return jdbcTemplate.query(SELECT_SQL, new BeanPropertyRowMapper<TransactionalMessageDO>(TransactionalMessageDO.class), messageType,
                createTime);
    }

    @Override
    public TransactionalMessageDO getById(long id) {
        return jdbcTemplate.queryForObject(SELECT_ID_SQL, new BeanPropertyRowMapper<TransactionalMessageDO>(TransactionalMessageDO.class),
                id);
    }
}
