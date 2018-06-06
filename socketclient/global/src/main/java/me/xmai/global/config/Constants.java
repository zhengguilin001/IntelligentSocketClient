package me.xmai.global.config;

import android.os.Environment;

/**
 * Created by alex on 16-11-16.
 */

public class Constants {

    /**
     * 通用常量
     */
    public interface COMMON {
        interface Url{
            //String sendImage ="http://httptest.iot08.com/sendImage";
            //String sendVoice = "http://httptest.iot08.com/sendAudio";
            String sendImage ="http://juhuo.iot08.com/sendImage";
            String sendVoice = "http://juhuo.iot08.com/sendAudio";
        }

        interface TYPE {
            int TYPE_LOGIN = 1;         //登录
            int TYPE_PULSE_C = 3;       //终端发送心跳
            int TYPE_CLOCK_SYNC = 5;    //终端请求时间同步
            int TYPE_NEW_MESSAGE = 11;  //查询新消息
            int TYPE_GET_WEATHER = 17;  //查询所在地天气
            int TYPE_ELECTRICITY = 21;  //电量上传
            int TYPE_STEP_COUNT = 41;   //上传计步
            int TYPE_LOCATION_C = 7;    //终端上传定位数据

            int TYPE_PULSE_S = 2;       //服务端发送心跳
            int TYPE_LOCATION_S = 6;    //服务器返回定位结果
            int TYPE_LOCK_S = 8;        //服务器请求定位
            int TYPE_SET_CONTACT_S = 16;//通讯录号码设置
            int TYPE_SET_SOS_NUMBER = 20;//SOS号码设置
            int TYPE_VOICE_MESSAGE =10; //新消息提醒（语音）

            int TYPE_SET_VOLUME = 26;     //音量级别设置
            int TYPE_SHUT_DOWN = 32;      //远程关机
            int TYPE_WATCH_MACTION = 34;  //监听设备
            int TYPE_NO_DISTURB = 28;     //免打扰时段设置
            int TYPE_TIMER_REMIND = 30;   //定时提醒设置

            int TYPE_TEXT_MESSAGE = 38;   //新消息提醒(文字)
            int TYPE_REMOTE_PHOTO = 36;   //远程拍照功能
            int TYPE_TIMER_LOCK = 42;      //设置终端定时上传定位
            int TYPE_CALL_STRATEGY = 24;   //拒绝陌生人来电设置
            int TYPE_MASTER_CLEAR = 44;    //恢复手表出厂设置
            int TYPE_DEVICE_TOKEN = 46;    //设备验证码
        }

        interface Gender {
            int TYPE_MEN = 0;
            int TYPE_WOMEN = 1;
            int TYPE_UNKNOWN = 2;
            int TYPE_STRANGE = 3;    //人妖
        }

        interface MSG {
            int MSG_SYNC_TOKEN = 0x01;
            int MSG_LOGIN = 0x02;
            int MSG_SYNC_CLOCK = 0x03;
            int MSG_LOCATION_SUC = 0x04;
            int MSG_GET_WEATHER = 0x05;
            int MSG_SHUT_DOWN  = 0x06;       //关机

            int MSG_SEND_VOICE = 0x07;
            int MSG_REMOTE_PHOTO = 0x08;    //远程拍照
            int MSG_UPLOAD_PHOTO = 0x09;    //上传照片

            int MSG_TIMER_LOCK = 0x10;      //定时上传定位
        }
    }

    public interface MODEL {

        interface ADMIN_TYPE {
            int TYPE_SUPER = 0;    // 超级管理员
            int TYPE_NORMAL = 1;    // 普通管理员
        }

        interface DATA {
            String DATA_TYPE = "type";
            String DATA_TOKEN = "token";
            String DATA_IDENT = "ident";         //指令标识
            String DATA_IMEI = "imei";           //设备imei
            String DATA_IMSI = "imsi";           //设备imsi
            String DATA_VERSION = "version";     //软件版本
            String DATA_MODULES = "modules";     //设备硬件配置模块
            String DATA_HEARTBEAT = "heartbeat"; //终端发送连接的心跳间隔(单位:秒)

            String DATA_MCC = "mcc";            //国家编号
            String DATA_MNC = "mnc";            //运营商编号
            String DATA_LAC = "lac";            //小区编号
            String DATA_CI = "ci";              //基站编号
            String DATA_RXLEV = "rxlev";        //信号频点,该值减去110 后得到信号强度,一般为负数
            String DATA_CELLS = "cells";        //基站数据列表

            String DATA_TIMESTAMP = "timestamp";         //采集数据时的unix 时间戳
            String DATA_SOS = "sos";                //当前定位是否为求救定位
            String DATA_FEEDBACK = "feedback";      //是否需要反馈定位结果(0:否, 1:是)

            String DATA_GPS = "gps";
            String DATA_LON = "lon";                //WGS84 坐标经度
            String DATA_LAT = "lat";                //WGS84 坐标纬度
            String DATA_HEIGHT = "height";          //高度
            String DATA_SPEED = "speed";            //速度
            String DATA_DIRECTION = "direction";    //方向

            String DATA_BATTERY = "battery";        //电量百分比(eg:70)
            String DATA_MAX_LEVEL = "max_level";    //最高电量等级(该字段高版本手表才有)
            String DATA_CURR_LEVEL = "curr_level";  //当前电量等级(该字段高版本手表才有)

            String DATA_STEP = "step";              //当前步数

            String DATA_LEVEL = "level";            //音量级别设置

            String DATA_PHONE = "phone";            //该监听用户的手机号码

            String DATA_INEDX = "index";            //当前数据索引值
            String DATA_LAST = "last";              //最后一组数据索引值
            String DATA_CONTACT = "contact";        //通讯录列表
            String DATA_NAME = "name";

            String DATA_DURATION ="duration";       //时长
            String DATA_SIZE = "size";              //语音文件大小
            String DATA_URL="url";                  //语音文件链接
            String DATA_ID = "id";                  //服务器返回ID
            String DATA_CONTENT = "content";        //文字消息

            String DATA_SECOND = "second";          //定期上传的周期秒数
        }

        interface PRENAME {
            String PRE_AMP = "amplocation";
            String PRE_AMPLAT = "amplat";
            String PRE_AMPLON = "amplon";
            String PRE_SOS = "issos";
        }

        interface SETTINGS{
            String GLOBAL_CITY  = "socket_client_city";                 //当前城市
            String GLOBAL_WEATHER = "socket_client_weather";            //天气数据
            String GLOBAL_TOKEN = "socket_client_shawn_token";                //token值
            String GLOBAL_PIC_PATH = "socket_client_picture_path";      //静默拍照路径
            String GLOBAL_ALARM = "socket_client_alarm";                //定时提醒
            String GLOBAL_DISTURB ="socket_client_disturb";             //免打扰设置
            String GLOBAL_CALLSTRATEGY = "socket_client_callstrategy";  //陌生人来电开关
            String GLOBAL_TIMER_LOCK = "socket_client_timerlock";       //定时上传定位时间间隔
        }

        interface SNAPPYDB{
            String DB_NAME = "wechat";              //snappyDB数据库名称
            String DB_MESSAGE_LIST = "messageList";      //存储键值对messageList
        }

        interface CACHEPATH {
            String CACHE = Environment.getExternalStorageDirectory()
                    .getPath() + "/socketclient";
        }
    }

}
