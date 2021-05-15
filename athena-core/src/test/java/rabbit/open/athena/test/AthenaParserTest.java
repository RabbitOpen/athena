package rabbit.open.athena.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.open.athena.plugin.common.meta.AthenaMetaData;

/**
 * 测试yml解析
 */
@RunWith(JUnit4.class)
public class AthenaParserTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void parseTest() {
        AthenaMetaData metaData = AthenaMetaData.readBy("athena-config.yml");
        logger.info("config: {}", metaData);
        TestCase.assertEquals(0, metaData.getEnabledPlugins().size());
        metaData = AthenaMetaData.readBy("empty.yml");
        TestCase.assertEquals(0, metaData.getEnabledPlugins().size());
    }

}
