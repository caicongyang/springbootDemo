package com.caicongyang.utils;

import com.caicongyang.Closer;
import com.caicongyang.ExceptionUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtils {
    private static Logger logger = LoggerFactory.getLogger(IOUtils.class);
    
    public static <T> T pipeStream(ExecutorService executor, Consumer<InputStream> iconsumer, Function<OutputStream, T> oconsumer) throws Exception {
        PipedInputStream pis = new PipedInputStream();
        try (PipedOutputStream pos = new PipedOutputStream(pis)) {
            Future<T> f = executor.submit(() -> oconsumer.apply(pos));
            executor.execute(() -> {
                try {
                    iconsumer.accept(pis);
                } catch (Exception e) {
                    logger.error("An exception occurred on read stream", e);
                } finally {
                    IOUtils.close(pis);
                }
            });
            return f.get();
        }
    }
    
    public static <T> T pipeStream(ExecutorService executor, Function<InputStream, T> iconsumer, Consumer<OutputStream> oconsumer) throws Exception {
        PipedOutputStream pos = new PipedOutputStream();
        try (PipedInputStream pis = new PipedInputStream(pos);) {
            Future<T> f = executor.submit(() -> iconsumer.apply(pis));
            executor.execute(() -> {
                try {
                    oconsumer.accept(pos);
                } catch (Exception e) {
                    logger.error("An exception occurred on write stream", e);
                } finally {
                    IOUtils.close(pos);
                }
            });
            return f.get();
        }
    }
    
    public static void pipeStream(Consumer<InputStream> iconsumer, Consumer<OutputStream> oconsumer) throws Exception {
        PipedInputStream pis = new PipedInputStream();
        try (PipedOutputStream pos = new PipedOutputStream(pis)) {
            Thread t = new Thread(() -> {
                try {
                    oconsumer.accept(pos);
                } finally {
                    IOUtils.close(pos);
                }
            });
            t.start();
            iconsumer.accept(pis);
            t.join();
        }
    }
    
	public static String getContent(InputStream is, Charset charset) {
		List<String> lines = getAllLines(is, charset);
		
		return linesToString(lines);
	}

	public static List<String> getAllLines(InputStream is, Charset charset) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, charset.name()));
			
			List<String> lines = new ArrayList<String>();
			
			String line = null;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Closer.close(br);
		}
	}
	
	
	private static String linesToString(List<String> lines) {
		StringBuilder content = new StringBuilder();
		int lineno = 1;
		for (String line : lines) {
			if (lineno > 1) content.append("\r\n");
			content.append(line);
		}
		return content.toString();
	}

	public static void close(Closeable... resources) {
		if (resources != null) {
			try {
				for (Closeable resource : resources) resource.close();
			} catch (Exception e) {
				ExceptionUtils.wrap2Runtime(e);
			}
		}
	}
	
	public static void close(AutoCloseable... resources) {
		if (resources != null) {
			try {
				for (AutoCloseable resource : resources) resource.close();
			} catch (Exception e) {
				ExceptionUtils.wrap2Runtime(e);
			}
		}
	}
	
	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				ExceptionUtils.wrap2Runtime(e);
			}
		}
	}
	
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				ExceptionUtils.wrap2Runtime(e);
			}
		}
	}
	
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				ExceptionUtils.wrap2Runtime(e);
			}
		}
	}
}
