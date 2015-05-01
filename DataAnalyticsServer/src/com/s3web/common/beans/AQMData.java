package com.s3web.common.beans;

import java.math.BigDecimal;

/*
 * jdbcTemplate
mysql> describe final_merged_aqmdata;
+------------------------+--------------+------+-----+---------+----------------+
| Field                  | Type         | Null | Key | Default | Extra          |
+------------------------+--------------+------+-----+---------+----------------+
| FMAQM_ID               | int(11)      | NO   | PRI | NULL    | auto_increment |
| datetime               | bigint(20)   | NO   |     | NULL    |                |
| block                  | varchar(64)  | NO   |     | NULL    |                |
| time                   | int(11)      | NO   |     | NULL    |                |
| smallParticle          | int(11)      | NO   |     | NULL    |                |
| largeParticle          | int(11)      | NO   |     | NULL    |                |
| conv_epa_smallParticle | decimal(7,4) | NO   |     | NULL    |                |
| conv_epa_largeParticle | decimal(7,4) | NO   |     | NULL    |                |
| ln_smallParticle       | decimal(7,4) | NO   |     | NULL    |                |
| ln_largeParticle       | decimal(7,4) | NO   |     | NULL    |                |
| minuteofday            | int(11)      | NO   |     | NULL    |                |
+------------------------+--------------+------+-----+---------+----------------+
11 rows in set (0.01 sec)


*
*/
public class AQMData {
	
	private int id;
	private long  datetime;
	private String block;
	private int time;
	private int smallParticle;
	private int largeParticle;
	private BigDecimal conv_epa_smallParticle;
	private BigDecimal conv_epa_largeParticle;
	private BigDecimal ln_smallParticle;
	private BigDecimal ln_largeParticle;
	private int minuteofday;
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getDatetime() {
		return datetime;
	}
	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}
	public String getBlock() {
		return block;
	}
	public void setBlock(String block) {
		this.block = block;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getSmallParticle() {
		return smallParticle;
	}
	public void setSmallParticle(int smallParticle) {
		this.smallParticle = smallParticle;
	}
	public int getLargeParticle() {
		return largeParticle;
	}
	public void setLargeParticle(int largeParticle) {
		this.largeParticle = largeParticle;
	}
	public BigDecimal getConv_epa_smallParticle() {
		return conv_epa_smallParticle;
	}
	public void setConv_epa_smallParticle(BigDecimal conv_epa_smallParticle) {
		this.conv_epa_smallParticle = conv_epa_smallParticle;
	}
	public BigDecimal getConv_epa_largeParticle() {
		return conv_epa_largeParticle;
	}
	public void setConv_epa_largeParticle(BigDecimal conv_epa_largeParticle) {
		this.conv_epa_largeParticle = conv_epa_largeParticle;
	}
	public BigDecimal getLn_smallParticle() {
		return ln_smallParticle;
	}
	public void setLn_smallParticle(BigDecimal ln_smallParticle) {
		this.ln_smallParticle = ln_smallParticle;
	}
	public BigDecimal getLn_largeParticle() {
		return ln_largeParticle;
	}
	public void setLn_largeParticle(BigDecimal ln_largeParticle) {
		this.ln_largeParticle = ln_largeParticle;
	}
	public int getMinuteofday() {
		return minuteofday;
	}
	public void setMinuteofday(int minuteofday) {
		this.minuteofday = minuteofday;
	}
	
	
}
