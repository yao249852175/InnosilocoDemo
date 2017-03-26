package innosiloco.demo.beans;

/**
 * Created by ronya on 2017/3/18.
 */

public class QuestionBean
{
    public final static byte QuestionStep_1 = 0x01;
    public final static byte QuestionStep_2 = 0x02;
    public final static byte QuestionStep_3 = 0x03;
    public final static byte QuestionStep_4 = 0x04;
    public final static byte QuestionStep_5 = 0x06;
    public final static byte QuestionResult = 0x5;

    /********************
     *  question 类型
     */
    public  byte type;

    public  String key;
    public boolean isSuccess = false;
    public boolean isQuestion = true;

    /**************
     *
     * @param type
     * @param key
     * @param isSuccess
     * @param isQuestion
     */
    public QuestionBean(byte type,String key,boolean isSuccess,boolean isQuestion)
    {
        this.type = type;
        this.key = key;
        this.isQuestion = isQuestion;
        this.isSuccess = isSuccess;

    }

    public  QuestionBean(){}
}
