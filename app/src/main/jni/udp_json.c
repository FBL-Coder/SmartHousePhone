#include "udp.h"
#include  "data_get.h"
#include "data_push.h"
#include "data_storage.h"

int timer_num; //i代表定时器的个数；t表示时间，逐秒递增
struct Timer { //Timer结构体，用来保存一个定时器的信息
    int total_time;  //每隔total_time秒
    int left_time;   //还剩left_time秒
    int func;        //该定时器超时，要执行的代码的标志
} myTimer[N];   //定义Timer类型的数组，用来保存所有的定时器

u8 ui_recvbuf[UI_BUFF_SIZE];
u8 recvbuf[BUFF_SIZE];

char local_ip[IP_SIZE];
SOCKET primary_udp;
SOCKET ui_socket;
struct sockaddr_in sender, ui_sender;
int sender_len, ui_sender_len;
static int devs_num = -1;
static int scene_num = -1;
static int board_out_num = -1;
static int board_input_num = -1;
static int keyOpitem_num = -1;


int ret_thrd1, ret_thrd2;
pthread_t thread1;
pthread_t thread2;
int pthread_flag;

ware_linked_list ware_list;
aircond_linked_list aircond_list;
light_linked_list light_list;
curtain_linked_list curtain_list;
tv_linked_list tv_list;
tvUP_linked_list tvUP_list;
lock_linked_list lock_list;
valve_linked_list valve_list;
frair_linked_list frair_list;
scene_linked_list scene_list;
rcu_linked_list rcu_list;
board_linked_list board_list;
keyinput_linked_list keyinput_list;
chnop_item_linked_list chnop_item_list;
keyop_item_linked_list keyop_item_list;

gw_client_linked_list gw_client_list;
app_client_linked_list app_client_list;
udp_msg_queue_linked_list msg_queue_list;

void udp_broadcast(u8 *devUnitID, u8 *devUnitPass) {
    UDPPROPKT *pkt = udp_pkt_bradcast(devUnitID, devUnitPass, "255.255.255.255");

    int optval = 1;//这个值一定要设置，否则可能导致sendto()失败
    setsockopt(primary_udp, SOL_SOCKET, SO_BROADCAST, &optval, sizeof(int));
    struct sockaddr_in theirAddr;
    memset(&theirAddr, 0, sizeof(struct sockaddr_in));
    theirAddr.sin_family = AF_INET;
    theirAddr.sin_addr.s_addr = inet_addr("255.255.255.255");
    theirAddr.sin_port = htons(ClOUD_SERVER_PORT);

    if ((sendto(primary_udp, pkt, sizeof(UDPPROPKT), 0,
                (struct sockaddr *) &theirAddr, sizeof(struct sockaddr))) == -1) {
        return;
    }
}

//5s search info
void get_info_from_gw_shorttime(int flag) {

    node_udp_msg_queue *head = msg_queue_list.head;

    for (; head; head = head->next) {
        if (head->flag == flag) {
            node_gw_client *gw_head = gw_client_list.head;
            for (; gw_head; gw_head = gw_head->next) {
                if (memcmp(head->devUnitID, gw_head->gw_id, 12) == 0) {
                    char rcu_ip[16] = {0};
                    sprintf(rcu_ip, "%d.%d.%d.%d", gw_head->rcu_ip[0], gw_head->rcu_ip[1],
                            gw_head->rcu_ip[2], gw_head->rcu_ip[3]);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr(rcu_ip), 0, 0, head->cmd,
                                                           gw_head->gw_id, gw_head->gw_pass, IS_ACK,
                                                           0, head->id);

                    for (int i = 0; i < 2; i++) {
                        sendto(primary_udp, (u8 *) send_pkt, sizeof(UDPPROPKT), 0,
                               (struct sockaddr *) &gw_head->gw_sender, sizeof(gw_head->gw_sender));
                    }
                }
            }
        }
    }
}

//30m search info
void get_info_from_gw_longtime() {
    node_udp_msg_queue *head = msg_queue_list.head;

    if (head->flag == -1) {
        node_gw_client *gw_head = gw_client_list.head;
        for (; gw_head; gw_head = gw_head->next) {
            for (; head; head = head->next) {
                if (memcmp(head->devUnitID, gw_head->gw_id, 12) == 0) {
                    char rcu_ip[16] = {0};
                    sprintf(rcu_ip, "%d.%d.%d.%d", gw_head->rcu_ip[0], gw_head->rcu_ip[1],
                            gw_head->rcu_ip[2], gw_head->rcu_ip[3]);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr(rcu_ip), 0, 0, head->cmd,
                                                           gw_head->gw_id, gw_head->gw_pass, IS_ACK,
                                                           0, head->id);
                    sendto(primary_udp, send_pkt, sizeof(UDPPROPKT), 0,
                           (struct sockaddr *) &gw_head->gw_sender, sizeof(gw_head->gw_sender));
                    sleep(1);
                }
            }
        }
    }
}

void setTimer(int time, int fun) //新建一个计时器
{
    struct Timer a;
    a.total_time = time;
    a.left_time = time;
    a.func = fun;
    myTimer[timer_num++] = a;
}

void timeout(int arg)  //判断定时器是否超时，以及超时时所要执行的动作
{
    int j;
    for (j = 0; j < timer_num; j++) {
        if (myTimer[j].left_time != 0)
            myTimer[j].left_time--;
        else {
            switch (myTimer[j].func) { //通过匹配myTimer[j].func，判断下一步选择哪种操作
                case 1:
                    for (int i = 0; i < 4; ++i) {
                        get_info_from_gw_shorttime(i);
                    }
                    break;
                case 4:
                    get_info_from_gw_longtime();
                    break;
            }
            myTimer[j].left_time = myTimer[j].total_time;     //循环计时
        }
    }
}

void *singal_msg(void *arg) {
    while (1) {
        sleep(1); //每隔一秒发送一个SIGALRM
        //kill(getpid(),SIGALRM);
        //arg = NULL;
        timeout((int) arg);
    }
}

void extract_json(u8 *buffer, SOCKADDR_IN send_client) {
    int devType;
    int devID;
    int subType2 = -1;
    u8 devUnitID[12];
    u8 devUnitPass[8];

    LOGI("接收到的数据:%s\n", buffer);
    //解析JSON数据
    cJSON *root_json = cJSON_Parse((char *) buffer);    //将字符串解析成json结构体
    if (NULL == root_json) {
        PR_ERR("error:%s\n", cJSON_GetErrorPtr());
        cJSON_Delete(root_json);
        return;
    }

    //devUnitID
    char *devUnitID_str = cJSON_GetObjectItem(root_json, "devUnitID")->valuestring;
    if (devUnitID_str != NULL) {
        string_to_bytes(devUnitID_str, devUnitID, 24);
        free(devUnitID_str);
    } else {
        return;
    }

    cJSON *devPass_json = cJSON_GetObjectItem(root_json, "devPass");
    if (devPass_json != NULL) {
        char *pass = devPass_json->valuestring;
        memcpy(devUnitPass, pass, 8);
        free(pass);
    }

    //datType
    int datType = cJSON_GetObjectItem(root_json, "datType")->valueint;
    switch (datType) {
        case e_udpPro_getBroadCast:
            app_client_list.app_client_add(&app_client_list, send_client, (u8 *) "",
                                           app_client_list.size);
            udp_broadcast(devUnitID, devUnitPass);
            break;
        case e_udpPro_getRcuInfo:
            udp_broadcast(devUnitID, devUnitPass);
            break;
        case e_udpPro_setRcuInfo: {
            u8 name[12];
            u8 IpAddr[4];
            u8 SubMask[4];
            u8 Gateway[4];
            u8 roomNum[4];
            u8 macAddr[6];
            u8 centerServ[4];

            memset(name, 0, 12);
            memset(IpAddr, 0, 4);
            memset(Gateway, 0, 4);
            memset(SubMask, 0, 4);
            memset(roomNum, 0, 4);
            memset(macAddr, 0, 6);
            memset(centerServ, 0, 4);

            char *name_str = cJSON_GetObjectItem(root_json, "name")->valuestring;
            if (name_str != NULL) {
                memcpy(name, name_str, 12);
                free(name_str);
            }
            char *ip_str = cJSON_GetObjectItem(root_json, "IpAddr")->valuestring;
            if (ip_str != NULL) {
                const char *div = ".";
                char *p;
                p = strtok(ip_str, div);
                int i = 0;
                while (p) {
                    IpAddr[i] = atoi(p);
                    i++;
                    p = strtok(NULL, div);
                }
                free(ip_str);
            }

            char *subMask_str = cJSON_GetObjectItem(root_json, "SubMask")->valuestring;
            if (subMask_str != NULL) {
                const char *div = ".";
                char *p;
                p = strtok(subMask_str, div);
                int i = 0;
                while (p) {
                    SubMask[i] = atoi(p);
                    i++;
                    p = strtok(NULL, div);
                }
                free(subMask_str);
            }

            char *gateway_str = cJSON_GetObjectItem(root_json, "Gateway")->valuestring;
            if (gateway_str != NULL) {
                const char *div = ".";
                char *p;
                p = strtok(gateway_str, div);
                int i = 0;
                while (p) {
                    Gateway[i] = atoi(p);
                    i++;
                    p = strtok(NULL, div);
                }
                free(gateway_str);
            }

            char *centerServ_str = cJSON_GetObjectItem(root_json, "centerServ")->valuestring;
            if (centerServ_str != NULL) {
                const char *div = ".";
                char *p;
                p = strtok(centerServ_str, div);
                int i = 0;
                while (p) {
                    centerServ[i] = atoi(p);
                    i++;
                    p = strtok(NULL, div);
                }
                free(centerServ_str);
            }

            char *roomNum_str = cJSON_GetObjectItem(root_json, "roomNum")->valuestring;
            if (roomNum_str != NULL) {
                memcpy(roomNum_str, roomNum, 4);
                free(roomNum_str);
            }

            char *macAddr_str = cJSON_GetObjectItem(root_json, "macAddr")->valuestring;
            if (macAddr_str != NULL) {
                string_to_bytes(macAddr_str, macAddr, 12);
                free(macAddr_str);
            }

            int bDhcp = cJSON_GetObjectItem(root_json, "bDhcp")->valueint;

            set_rcuInfo_json(devUnitID, devUnitPass, name, IpAddr, SubMask, Gateway, centerServ,
                             roomNum, macAddr, bDhcp);
        }
            break;
        case e_udpPro_handShake:
            break;
        case e_udpPro_getDevsInfo:
            get_info_from_gw_shorttime(1);
            break;
        case e_udpPro_addDev: {
            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            u8 devName[12];
            u8 roomName[12];
            u8 canCpuID[12];
            memset(devName, 0, 12);
            memset(roomName, 0, 12);

            char *devName_str = cJSON_GetObjectItem(root_json, "devName")->valuestring;
            if (devName_str != NULL) {
                string_to_bytes(devName_str, devName, strlen(devName_str));
                free(devName_str);
            } else {
                return;
            }
            char *roomName_str = cJSON_GetObjectItem(root_json, "roomName")->valuestring;
            if (roomName_str != NULL) {
                string_to_bytes(roomName_str, roomName, strlen(roomName_str));
                free(roomName_str);
            } else {
                return;
            }

            int powChn = cJSON_GetObjectItem(root_json, "powChn")->valueint - 1;
            char *canCpuID_str = cJSON_GetObjectItem(root_json, "canCpuID")->valuestring;
            if (canCpuID_str != NULL) {
                string_to_bytes(canCpuID_str, canCpuID, 24);
                free(canCpuID_str);
            } else {
                return;
            }

            add_devs_json(devUnitID, canCpuID, e_udpPro_addDev, devType, powChn, roomName, devName);
        }
            break;
        case e_udpPro_editDev: {
            //重新打包数据，转发给联网模块
            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            devID = cJSON_GetObjectItem(root_json, "devID")->valueint;
            //控制时cmd为控制命令，编辑和删除时cmd为设备类型T_WARE_TYPE
            int cmd = cJSON_GetObjectItem(root_json, "cmd")->valueint;
            u8 canCpuID[12];
            u8 devName[12];
            u8 roomName[12];
            memset(devName, 0, 12);
            memset(roomName, 0, 12);
            char *canCpuID_str = cJSON_GetObjectItem(root_json, "canCpuID")->valuestring;
            if (canCpuID_str != NULL) {
                string_to_bytes(canCpuID_str, canCpuID, 24);
                free(canCpuID_str);
            } else {
                return;
            }
            char *devName_str = cJSON_GetObjectItem(root_json, "devName")->valuestring;
            if (devName_str != NULL) {
                string_to_bytes(devName_str, devName, strlen(devName_str));
                free(devName_str);
            } else {
                return;
            }
            char *roomName_str = cJSON_GetObjectItem(root_json, "roomName")->valuestring;
            if (roomName_str != NULL) {
                string_to_bytes(roomName_str, roomName, strlen(roomName_str));
                free(roomName_str);
            } else {
                return;
            }

            int powChn = cJSON_GetObjectItem(root_json, "powChn")->valueint - 1;
            ctrl_devs_json(devUnitID, canCpuID, e_udpPro_editDev, devType, devID, powChn, devName,
                           roomName, cmd);
        }
            break;
        case e_udpPro_delDev: {
            //重新打包数据，转发给联网模块
            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            devID = cJSON_GetObjectItem(root_json, "devID")->valueint;
            //控制时cmd为控制命令，编辑和删除时cmd为设备类型T_WARE_TYPE
            int cmd = cJSON_GetObjectItem(root_json, "cmd")->valueint;

            u8 canCpuID[12];
            char *canCpuID_str = cJSON_GetObjectItem(root_json, "canCpuID")->valuestring;
            if (canCpuID_str != NULL) {
                string_to_bytes(canCpuID_str, canCpuID, 24);
                free(canCpuID_str);
            } else {
                return;
            }
            ctrl_devs_json(devUnitID, canCpuID, e_udpPro_delDev, devType, devID, 0, 0, 0, cmd);
        }
            break;
        case e_udpPro_ctrlDev: {//重新打包数据，转发给联网模块
            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            devID = cJSON_GetObjectItem(root_json, "devID")->valueint;
            //控制时cmd为控制命令，编辑和删除时cmd为设备类型T_WARE_TYPE
            int cmd = cJSON_GetObjectItem(root_json, "cmd")->valueint;

            u8 canCpuID[12];
            char *canCpuID_str = cJSON_GetObjectItem(root_json, "canCpuID")->valuestring;
            if (canCpuID_str != NULL) {
                string_to_bytes(canCpuID_str, canCpuID, 24);
                free(canCpuID_str);
            } else {
                return;
            }
            ctrl_devs_json(devUnitID, canCpuID, e_udpPro_ctrlDev, devType, devID, 0, 0, 0, cmd);
        }
            break;
        case e_udpPro_ctrl_allDevs:
            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            int ctrl_cmd = cJSON_GetObjectItem(root_json, "cmd")->valueint;

            ctrl_all_devs_json(devUnitID, e_udpPro_ctrl_allDevs, devType, ctrl_cmd);
            break;
        case e_udpPro_getBoards: {
            subType2 = cJSON_GetObjectItem(root_json, "subType2")->valueint;
            if (subType2 == e_board_chnOut) {
                get_info_from_gw_shorttime(3);
            }
            if (subType2 == e_board_keyInput) {
                get_info_from_gw_shorttime(4);
            }
        }
            break;
        case e_udpPro_getSceneEvents:
            get_info_from_gw_shorttime(2);
            break;

        case e_udpPro_addSceneEvents:
        case e_udpPro_editSceneEvents:
        case e_udpPro_delSceneEvents: {
            char *sceneName = cJSON_GetObjectItem(root_json, "sceneName")->valuestring;
            int devCnt = cJSON_GetObjectItem(root_json, "devCnt")->valueint;
            int eventId = cJSON_GetObjectItem(root_json, "eventId")->valueint;
            int datType = cJSON_GetObjectItem(root_json, "datType")->valueint;
            cJSON *itemAry = cJSON_GetObjectItem(root_json, "itemAry");

            add_scene_json(devUnitID, sceneName, devCnt, eventId, itemAry, datType);
        }
            break;
        case e_udpPro_exeSceneEvents: {
            int eventId = cJSON_GetObjectItem(root_json, "eventId")->valueint;
            int datType = cJSON_GetObjectItem(root_json, "datType")->valueint;
            ctrl_scene_json(devUnitID, eventId, datType);
            break;
        }
        case e_udpPro_getKeyOpItems: {
            char *canCpuID_str = cJSON_GetObjectItem(root_json, "uid")->valuestring;
            if (canCpuID_str == NULL)
                return;
            u8 uid[12];
            string_to_bytes(canCpuID_str, uid, 24);

            int key_index = cJSON_GetObjectItem(root_json, "key_index")->valueint;
            get_keyOpitem_json(devUnitID, key_index, uid);
        }
            break;
        case e_udpPro_setKeyOpItems: {

            char *cpuCanID_str = cJSON_GetObjectItem(root_json, "key_cpuCanID")->valuestring;
            u8 canCpuID[12];
            string_to_bytes(cpuCanID_str, canCpuID, 24);

            int key_index = cJSON_GetObjectItem(root_json, "key_index")->valueint;
            int cnt = cJSON_GetObjectItem(root_json, "cnt")->valueint;
            cJSON *keyop_item = cJSON_GetObjectItem(root_json, "key_opitem_rows");

            set_key_opitem_json(devUnitID, canCpuID, key_index, cnt, keyop_item,
                                e_udpPro_setKeyOpItems);
        }
            break;
        case e_udpPro_delKeyOpItems: {

            char *cpuCanID_str = cJSON_GetObjectItem(root_json, "key_cpuCanID")->valuestring;
            u8 canCpuID[12];
            string_to_bytes(cpuCanID_str, canCpuID, 24);

            int key_index = cJSON_GetObjectItem(root_json, "key_index")->valueint;
            int cnt = cJSON_GetObjectItem(root_json, "cnt")->valueint;
            cJSON *keyop_item = cJSON_GetObjectItem(root_json, "key_opitem_rows");

            set_key_opitem_json(devUnitID, canCpuID, key_index, cnt, keyop_item,
                                e_udpPro_delKeyOpItems);
        }
            break;
        case e_udpPro_getChnOpItems: {
            char *cpuCanID_str = cJSON_GetObjectItem(root_json, "uid")->valuestring;
            u8 canCpuID[12];
            string_to_bytes(cpuCanID_str, canCpuID, 24);

            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            devID = cJSON_GetObjectItem(root_json, "devID")->valueint;

            get_chnOpitem_json(devUnitID, devType, devID, canCpuID);
        }
            break;
        case e_udpPro_setChnOpItems: {
            char *cpuCanID_str = cJSON_GetObjectItem(root_json, "out_cpuCanID")->valuestring;
            u8 canCpuID[12];
            string_to_bytes(cpuCanID_str, canCpuID, 24);

            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            devID = cJSON_GetObjectItem(root_json, "devID")->valueint;
            int cnt = cJSON_GetObjectItem(root_json, "chn_opitem")->valueint;
            cJSON *chnop_item = cJSON_GetObjectItem(root_json, "chn_opitem_rows");

            set_chn_opitem_json(devUnitID, canCpuID, devType, devID, cnt, chnop_item,
                                e_udpPro_setChnOpItems);
        }
            break;
        case e_udpPro_delChnOpItems: {
            char *cpuCanID_str = cJSON_GetObjectItem(root_json, "out_cpuCanID")->valuestring;
            u8 canCpuID[12];
            string_to_bytes(cpuCanID_str, canCpuID, 24);

            devType = cJSON_GetObjectItem(root_json, "devType")->valueint;
            devID = cJSON_GetObjectItem(root_json, "devID")->valueint;
            int cnt = cJSON_GetObjectItem(root_json, "chn_opitem")->valueint;
            cJSON *chnop_item = cJSON_GetObjectItem(root_json, "chn_opitem_rows");

            set_chn_opitem_json(devUnitID, canCpuID, devType, devID, cnt, chnop_item,
                                e_udpPro_delChnOpItems);
        }
            break;

        default:
            break;
    }
}

void extract_data(UDPPROPKT *udp_pro_pkt, int dat_len, SOCKADDR_IN sender) {
    if (udp_pro_pkt->bAck == 1) {
        // Log.e(TAG, "需要应答的数据包");
        udp_pro_pkt->bAck = NOW_ACK;
        u8 temp[4];

        memcpy(temp, udp_pro_pkt->dstIp, 4);
        memcpy(udp_pro_pkt->dstIp, udp_pro_pkt->srcIp, 4);
        memcpy(udp_pro_pkt->srcIp, temp, 4);

        sendto(primary_udp, (u8 *) udp_pro_pkt, dat_len, 0, (struct sockaddr *) &sender,
               sizeof(sender));
    }

    if (udp_pro_pkt->datType != 2) {
        LOGI("收到的数据包:%d %d %d", udp_pro_pkt->datType, udp_pro_pkt->subType1, udp_pro_pkt->subType2);
    }

    switch (udp_pro_pkt->datType) {
        case e_udpPro_getRcuInfo:          // e_udpPro_getRcuinfo
            if (udp_pro_pkt->subType2 == 1) {
                //联网模块发送信息到服务器
                set_rcuinfo(udp_pro_pkt, sender);
                //给UI发送收到消息info
                report_rcu_info_json(udp_pro_pkt);

                msg_queue_list.udp_msg_queue_add(&msg_queue_list, udp_pro_pkt->uidSrc,
                                                 e_udpPro_getDevsInfo, 0,
                                                 1,
                                                 msg_queue_list.size);

                msg_queue_list.udp_msg_queue_add(&msg_queue_list, udp_pro_pkt->uidSrc,
                                                 e_udpPro_getSceneEvents,
                                                 0, 2, msg_queue_list.size);

                msg_queue_list.udp_msg_queue_add(&msg_queue_list, udp_pro_pkt->uidSrc,
                                                 e_udpPro_getBoards,
                                                 e_board_chnOut, 3, msg_queue_list.size);
                msg_queue_list.udp_msg_queue_add(&msg_queue_list, udp_pro_pkt->uidSrc,
                                                 e_udpPro_getBoards,
                                                 e_board_keyInput, 4,
                                                 msg_queue_list.size);

                get_info_from_gw_shorttime(1);
                get_info_from_gw_shorttime(2);
                get_info_from_gw_shorttime(3);
                get_info_from_gw_shorttime(4);

                setTimer(60 * 2, 1);

                if (pthread_flag == 0) {
                    ret_thrd2 = pthread_create(&thread2, NULL, singal_msg, NULL);
                    // 线程创建成功，返回0,失败返回失败号
                    if (ret_thrd2 == 0) {
                        pthread_flag = 1;
                    }
                }
            }
            break;
        case e_udpPro_handShake: //握手应答
            handShake(udp_pro_pkt);
            break;

        case e_udpPro_getDevsInfo:
            if (udp_pro_pkt->subType1 == 1) {
                set_dev_info(udp_pro_pkt);

                if (devs_num != ware_list.size) {
                    report_all_devs_info_json(udp_pro_pkt->uidSrc);
                    devs_num = ware_list.size;
                }
            }
            break;

        case e_udpPro_addDev:
        case e_udpPro_ctrlDev:
        case e_udpPro_editDev:
            if (udp_pro_pkt->subType2 == 1) {
                //控制设备之后，返回设备的最新数据，直接更新链表
                //如果是编辑，并且同devUnitID，同devType，powChn则删除原来的设备，再添加新设备
                if (udp_pro_pkt->datType == e_udpPro_editDev) {
                    WARE_DEV *ware_dev = (WARE_DEV *) udp_pro_pkt->dat;
                    switch (ware_dev->devType) {
                        case e_ware_airCond:
                            aircond_list.ware_aircond_clean(&aircond_list);
                            break;
                        case e_ware_light:
                            light_list.ware_light_clean(&light_list);
                            break;
                        case e_ware_curtain:
                            curtain_list.ware_curtain_clean(&curtain_list);
                            break;
                        case e_ware_lock:
                            lock_list.ware_lock_clean(&lock_list);
                            break;
                        case e_ware_value:
                            valve_list.ware_valve_clean(&valve_list);
                            break;
                        case e_ware_fresh_air:
                            frair_list.ware_frair_clean(&frair_list);
                            break;
                    }
                    get_info_from_gw_shorttime(1); //重新获取所有设备
                } else {
                    fresh_dev_info(udp_pro_pkt);
                }
                report_all_ctl_reply_json(udp_pro_pkt);
            }
            break;

        case e_udpPro_delDev:
            if (udp_pro_pkt->subType1 == 1) {
                del_dev_info(udp_pro_pkt);
                //转发该数据包到所有app
                report_all_ctl_reply_json(udp_pro_pkt);
                get_info_from_gw_shorttime(1); //重新获取所有设备
            }
            break;

        case e_udpPro_getSceneEvents:
            if (udp_pro_pkt->subType2 == 1) {
                set_events_info(udp_pro_pkt);

                if (scene_num != scene_list.size) {
                    report_scene_ctl_reply_json(udp_pro_pkt);
                    scene_num = scene_list.size;
                }
            }
            break;

        case e_udpPro_addSceneEvents:
        case e_udpPro_editSceneEvents:
        case e_udpPro_exeSceneEvents:
            if (udp_pro_pkt->subType1 == 0 && udp_pro_pkt->subType2 == 1) {
                set_events_info(udp_pro_pkt);
                report_scene_ctl_reply_json(udp_pro_pkt);
            }
            break;

        case e_udpPro_delSceneEvents:
            if (udp_pro_pkt->subType1 == 0 && udp_pro_pkt->subType2 == 1) {
                del_scene_info(udp_pro_pkt);
                report_scene_ctl_reply_json(udp_pro_pkt);//客户端需要删除该情景模式
            }
            break;
        case e_udpPro_getBoards:
            if (udp_pro_pkt->subType1 == 1) {
                set_board_info(udp_pro_pkt);
                BOARD_CHNOUT *board = (BOARD_CHNOUT *) udp_pro_pkt->dat; //四种类型结构一样

                switch (board->boardType) {
                    case e_board_chnOut:
                        if (board_out_num != board_list.size) {
                            board_out_num = board_list.size;
                            report_all_board_chnout_json(udp_pro_pkt->uidSrc);
                        }
                        break;
                    case e_board_keyInput:
                        if (board_input_num != keyinput_list.size) {
                            board_input_num = keyinput_list.size;
                            report_all_board_keyinput_json(udp_pro_pkt->uidSrc);
                        }
                }
            }
            break;

        case e_udpPro_editBoards:
            if (udp_pro_pkt->subType1 == 1) {
                if (udp_pro_pkt->subType2 == 1) {
                    set_board_info(udp_pro_pkt);
                }
            }
            break;

        case e_udpPro_delBoards:
            if (udp_pro_pkt->subType1 == 1) {
                if (udp_pro_pkt->subType2 == 1) {
                    del_board_info(udp_pro_pkt);
                }
            }
            break;

        case e_udpPro_getKeyOpItems:
            if (udp_pro_pkt->subType1 == 1) {
                keyOpitem_num = -1;
                set_key_opitem(udp_pro_pkt);
                report_key_opitem_json(udp_pro_pkt->uidSrc, udp_pro_pkt->dat,
                                       udp_pro_pkt->subType2);
            }
            break;
        case e_udpPro_setKeyOpItems:
            if (udp_pro_pkt->subType1 == 1) {
                int result = udp_pro_pkt->dat[12];
                if (result == 1) {
                    set_key_opitem_reply_json(udp_pro_pkt);
                }
            }
            break;
        case e_udpPro_delKeyOpItems:
            if (udp_pro_pkt->subType1 == 1) {
                int result = (int) udp_pro_pkt->dat[12];
                if (result == 1) {
                    del_key_opitem(udp_pro_pkt);
                }
                set_key_opitem_reply_json(udp_pro_pkt);
                keyOpitem_num = -1;
            }
            break;
        case e_udpPro_getChnOpItems:
            if (udp_pro_pkt->subType1 == 1) {
                set_chn_opitem(udp_pro_pkt);
                u8 uid[12];
                u8 devType;
                u8 devID;
                memcpy(uid, udp_pro_pkt->dat, 12);
                devType = udp_pro_pkt->dat[12];
                devID = udp_pro_pkt->dat[13];

                report_chn_opitem_json(udp_pro_pkt->uidSrc, uid, devType, devID);
            }

            break;

        case e_udpPro_setChnOpItems:
            if (udp_pro_pkt->subType1 == 1) {
                int result = (int) udp_pro_pkt->dat[12];
                if (result == 1) {
                    set_chn_opitem(udp_pro_pkt);
                }
                set_chn_opitem_reply_json(udp_pro_pkt);
            }
            break;

        case e_udpPro_delChnOpItems:
            if (udp_pro_pkt->subType1 == 1) {
                int result = (int) udp_pro_pkt->dat[12];
                if (result == 1) {
                    del_chn_opitem(udp_pro_pkt);
                }
                set_chn_opitem_reply_json(udp_pro_pkt);
            }
            break;
        case e_udpPro_chns_status:
            ctrl_dev_reply(udp_pro_pkt);
            get_light_ctr_reply_json(udp_pro_pkt);
            break;
        default:
            break;
    }
}

void *ui_udp_server() {

    ui_socket = init_socket(SOCK_TYPE); //SOCK_DGRAM

    struct sockaddr_in local_ui_socket;
    local_ui_socket.sin_family = AF_INET;
    local_ui_socket.sin_port = htons(UI_SERVER_PORT);
    local_ui_socket.sin_addr.s_addr = htonl(INADDR_ANY);

    int result = bind(ui_socket, (struct sockaddr *) &local_ui_socket, sizeof(local_ui_socket));
    if (result == SOCKET_ERROR) {
        return NULL;
    }

    memset(&ui_recvbuf, 0, sizeof(ui_recvbuf));

    while (1) {
        ui_sender_len = sizeof(ui_sender);
        int ret = recvfrom(ui_socket, (u8 *) &ui_recvbuf, UI_BUFF_SIZE, 0,
                           (struct sockaddr *) &ui_sender,
                           (socklen_t *) &ui_sender_len);

        if (ret <= 0) {
            continue;
        } else {
            extract_json(ui_recvbuf, ui_sender);
            memset(&ui_recvbuf, 0, sizeof(ui_recvbuf));
        }
    }
}

void udp_server(char *ip) {
    primary_udp = init_socket(SOCK_TYPE); //SOCK_DGRAM

    struct sockaddr_in local;
    local.sin_family = AF_INET;
    local.sin_port = htons(ClOUD_SERVER_PORT);
    local.sin_addr.s_addr = htonl(INADDR_ANY);

    int result = bind(primary_udp, (struct sockaddr *) &local, sizeof(local));
    if (result == SOCKET_ERROR) {
        PR_ERR("bind");
    }


    if (strcmp(ip, "") == 0) {
        get_local_ip("wlan0", local_ip);
        LOGI("本机IP:%s\n", local_ip);
    }
    else
        memcpy(local_ip, ip, 16);

    memset(&recvbuf, 0, sizeof(recvbuf));

    timer_num = 0;
    pthread_flag = 0;

    rcu_list = rcu_create_linked_list();
    ware_list = ware_create_linked_list();
    aircond_list = ware_aircond_create_linked_list();
    light_list = ware_light_create_linked_list();
    curtain_list = ware_curtain_create_linked_list();
    lock_list = ware_lock_create_linked_list();
    valve_list = ware_valve_create_linked_list();
    frair_list = ware_frair_create_linked_list();
    tv_list = ware_tv_create_linked_list();
    tvUP_list = ware_tvUP_create_linked_list();
    scene_list = ware_scene_create_linked_list();
    board_list = board_create_linked_list();
    keyinput_list = keyinput_create_linked_list();
    chnop_item_list = chnop_item_create_linked_list();
    keyop_item_list = keyop_item_create_linked_list();
    gw_client_list = gw_client_create_linked_list();
    app_client_list = app_client_create_linked_list();
    msg_queue_list = udp_msg_queue_create_linked_list();


    ret_thrd1 = pthread_create(&thread1, NULL, ui_udp_server, NULL);
    // 线程创建成功，返回0,失败返回失败号
    if (ret_thrd1 != 0) {
        return;
    } else {
        LOGI("创建UI_SERVER线程成功");
    }

    while (1) {

        sender_len = sizeof(sender);

        int ret = recvfrom(primary_udp, (u8 *) &recvbuf, BUFF_SIZE, 0,
                           (struct sockaddr *) &sender, &sender_len);

        if (ret <= 0) {
            continue;
        } else {
            /* 显示client端的网络地址*/
            if (memcmp(inet_ntoa(sender.sin_addr), local_ip, 16) == 0)
                continue;
            extract_data((UDPPROPKT *) recvbuf, ret, sender);
            memset(&recvbuf, 0, sizeof(recvbuf));
        }
    }
}
