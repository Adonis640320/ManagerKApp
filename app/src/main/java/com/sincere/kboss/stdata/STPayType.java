package com.sincere.kboss.stdata;

import com.sincere.kboss.KbossApplication;

/**
 * Created by Michael on 10/29/2016.
 */
public class STPayType {
    public int f_id;
    public String f_name;
    public String f_subname;
    public String f_detail;
    public double f_cancelled_percent;

    public static double getCancelledPercent(int pay_type) {
        if (KbossApplication.g_paytypes == null || KbossApplication.g_paytypes.isEmpty()) {
            return 0.0;
        }

        for (int i=0; i<KbossApplication.g_paytypes.size(); i++) {
            STPayType payType = KbossApplication.g_paytypes.get(i);
            if (payType.f_id == pay_type) {
                return payType.f_cancelled_percent;
            }
        }

        return 0.0;
    }
}
