package com.caicongyang;

import com.caicongyang.utils.IOUtils;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class Closer {

	public static void close(Closeable... resources) {
		IOUtils.close(resources);
	}
	
	public static void close(AutoCloseable... resources) {
		IOUtils.close(resources);
	}
	
	public static void close(Statement stmt) {
		IOUtils.close(stmt);
	}
	
	public static void close(ResultSet rs) {
		IOUtils.close(rs);
	}
	
	public static void close(Connection conn) {
		IOUtils.close(conn);
	}
}
