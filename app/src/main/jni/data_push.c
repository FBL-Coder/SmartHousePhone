#include "data_push.h"

void report_broadcast_info_json(UDPPROPKT *pkt) {

    char *json_str = create_broadcast_json(pkt->uidSrc, e_udpPro_getBroadCast, 0, 0);
    int len = strlen(json_str);

    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, len, 0, (struct sockaddr *) &head->app_sender,
               sizeof(head->app_sender));
    }
    free(json_str);
}

void report_rcu_info_json(UDPPROPKT *pkt) {

    char *json_str = create_rcu_json(pkt->uidSrc, e_udpPro_getRcuInfo, 0, 1);
    int len = strlen(json_str);

    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, len, 0, (struct sockaddr *) &head->app_sender,
               sizeof(head->app_sender));
    }
    free(json_str);
}

void get_devs_info_json(u8 *devUnitID, SOCKADDR_IN sender_client) {
    char *json_str = create_dev_json(devUnitID, e_udpPro_getDevsInfo, 1, 0);
    int len = strlen(json_str);

    sendto(primary_udp, json_str, len, 0, (struct sockaddr *) &sender_client,
           sizeof(sender_client));
    free(json_str);
}

void report_all_devs_info_json(u8 *devUnitID) {
    char *json_str = create_dev_json(devUnitID, e_udpPro_getDevsInfo, 1, 0);

    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }

    free(json_str);
}

void get_events_info_json(u8 *devUnitID, SOCKADDR_IN sender_client) {
    char *json_str = create_events_json(devUnitID, e_udpPro_getSceneEvents, 0, 1);

    sendto(primary_udp, json_str, strlen(json_str), 0, (struct sockaddr *) &sender_client,
           sizeof(sender_client));
    free(json_str);
}

void get_board_chnout_json(u8 *devUnitID, SOCKADDR_IN sender_client) {
    char *json_str = create_board_chnout_json(devUnitID, e_udpPro_getBoards, 1, e_board_chnOut);
    sendto(primary_udp, json_str, strlen(json_str), 0, (struct sockaddr *) &sender_client,
           sizeof(sender_client));
    free(json_str);
}

void report_all_board_chnout_json(u8 *devUnitID) {
    char *json_str = create_board_chnout_json(devUnitID, e_udpPro_getBoards, 1, e_board_chnOut);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }
    free(json_str);
}

void report_all_board_keyinput_json(u8 *devUnitID) {
    char *json_str = create_board_keyinput_json(devUnitID, e_udpPro_getBoards, 1,
                                                e_board_keyInput);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }
    free(json_str);
}

void get_board_keyinput_json(u8 *devUnitID, SOCKADDR_IN sender_client) {
    char *json_str = create_board_keyinput_json(devUnitID, e_udpPro_getBoards, 1,
                                                e_board_keyInput);

    sendto(primary_udp, json_str, strlen(json_str), 0, (struct sockaddr *) &sender_client,
           sizeof(sender_client));
    free(json_str);
}

void report_key_opitem_json(u8 *devUnitID, u8 *cpuCanID, int key_index) {
    char *json_str = create_get_key_opitem_reply_json(devUnitID, cpuCanID, key_index);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0, (struct sockaddr *) &head->app_sender,
               sizeof(head->app_sender));
    }
    free(json_str);
}

void report_chn_opitem_json(u8 *devUnitID, u8 *cpuCanID, int devType, int devID) {
    char *json_str = create_get_chn_opitem_reply_json(devUnitID, cpuCanID, devType, devID);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0, (struct sockaddr *) &head->app_sender,
               sizeof(head->app_sender));
    }
    free(json_str);
}

void get_chn_opitem_json(u8 *devUnitID, u8 *cpuCanID, int devType, int devID,
                         SOCKADDR_IN sender_client) {
    char *json_str = create_get_chn_opitem_reply_json(devUnitID, cpuCanID, devType, devID);

    sendto(primary_udp, json_str, strlen(json_str), 0, (struct sockaddr *) &sender_client,
           sizeof(sender_client));
    free(json_str);
}

void report_all_ctl_reply_json(UDPPROPKT *pkt) {
    char *json_str = create_ctl_reply_info_json(pkt);

    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }

    free(json_str);
}

void get_light_ctr_reply_json(UDPPROPKT *pkt) {
    char *json_str = create_chn_status_json(pkt->uidSrc, e_udpPro_chns_status, 1, 1);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }
    free(json_str);
}

void report_scene_ctl_reply_json(UDPPROPKT *pkt) {
    char *json_str = create_events_json(pkt->uidSrc, pkt->datType, 0, 1);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }
    free(json_str);
}

void set_chn_opitem_reply_json(UDPPROPKT *pkt) {
    char *json_str = create_set_chn_opitem_reply_json(pkt);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }
    free(json_str);
}

void set_key_opitem_reply_json(UDPPROPKT *pkt) {
    char *json_str = create_set_key_opitem_reply_json(pkt);
    node_app_client *head = app_client_list.head;
    for (; head; head = head->next) {
        sendto(primary_udp, json_str, strlen(json_str), 0,
               (struct sockaddr *) &head->app_sender, sizeof(head->app_sender));
    }
    free(json_str);
}