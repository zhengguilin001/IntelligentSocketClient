package com.xmaihh.voice;

/**
 * RecordStrategy 录音策略接口
 *
 * @author acer
 */
public interface RecordStrategy {

    /**
     * 在这里进行录音准备工作，重置录音文件名等
     */
    void ready();

    /**
     * 开始录音
     */
    void start();

    /**
     * 录音结束
     */
    void stop();

    /**
     * 录音失败时删除原来的旧文件
     */
    void deleteOldFile();

    /**
     * 获取录音音量的大小
     *
     * @return
     */
    double getAmplitude();

    /**
     * 返回录音文件完整路径
     *
     * @return
     */
    String getFilePath();

}
