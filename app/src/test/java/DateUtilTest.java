import com.github.lzyzsd.androidstockchart.DateUtil;

import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by Bruce on 2/28/15.
 */
public class DateUtilTest {
    @Test
    public void test_parse_date() {
        DateTime dateTime = DateUtil.parse_yyyyddMM_hhmmss("20150227 06:00:00");
        assertThat(dateTime.getYear()).isEqualTo(2015);
        assertThat(dateTime.getMonthOfYear()).isEqualTo(2);
        assertThat(dateTime.getDayOfMonth()).isEqualTo(27);
        assertThat(dateTime.getHourOfDay()).isEqualTo(6);
    }

    @Test
    public void test_format_hh_mm() {
        DateTime dateTime = DateTime.now();
        String str = DateUtil.format_hh_mm(dateTime);
        assertThat(str).isEqualTo(dateTime.getHourOfDay()+":"+dateTime.getMinuteOfHour());
    }

    @Test
    public void should_format_hh_mm_fail() {
    }

    @Test
    public void test_getPoints4OneTradeDayFromBondCategory() {
        int size = DateUtil.getPoints4OneTradeDayFromBondCategory("1260-1440;0-150;540-690;810-930");
        assertThat(size).isEqualTo(600);
    }

    @Test
    public void test_getStartLabelFromBondCategory() {
        String label = DateUtil.getStartLabelFromBondCategory("1260-1440;0-150;540-690;810-930");
        assertThat(label).isEqualTo("21:00");
    }

    @Test
    public void test_getEndLabelFromBondCategory() {
        String label = DateUtil.getEndLabelFromBondCategory("1260-1440;0-150;540-690;810-930");
        assertThat(label).isEqualTo("15:30");
    }
}
