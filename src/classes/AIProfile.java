package classes;

/**
 * Created by JD Isenhart on 4/11/2017.
 * Testing RMI creation in Java 8
 */
public class AIProfile {

    private float activity, aggressiveness, responsiveness;

    public AIProfile(float activity, float aggressiveness, float responsiveness) {
        this.activity = activity;
        this.aggressiveness = aggressiveness;
        this.responsiveness = responsiveness;
    }

    public String getDesc() {
        StringBuilder desc = new StringBuilder();

        switch (roundFloat(responsiveness, 2)) {
            case 0:
                desc.append("Slow, ");
                break;
            case 1:
                desc.append("Average, ");
                break;
            case 2:
                desc.append("Quick, ");
                break;
            default:
                desc.append("N/A, ");
                break;

        }

        switch (roundFloat(aggressiveness, 2)) {
            case 0:
                desc.append("Relaxed, ");
                break;
            case 1:
                desc.append("Competitive, ");
                break;
            case 2:
                desc.append("Assertive, ");
                break;
            default:
                desc.append("N/A, ");
                break;

        }

        switch (roundFloat(activity, 2)) {
            case 0:
                desc.append("Follower, ");
                break;
            case 1:
                desc.append("Fair, ");
                break;
            case 2:
                desc.append("Driver, ");
                break;
            default:
                desc.append("N/A, ");
                break;

        }
        return desc.toString();
    }

    private int roundFloat(float f, int fraction) {
        return Math.round(f * fraction);
    }

    public float getActivity() {
        return activity;
    }

    public float getAggressiveness() {
        return aggressiveness;
    }

    public float getResponsiveness() {
        return responsiveness;
    }


}
