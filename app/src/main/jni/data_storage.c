#include "data_storage.h"

void set_rcuinfo(UDPPROPKT *pkt, SOCKADDR_IN sender) {
    RCU_INFO *rcuinfo = (RCU_INFO *) pkt->dat;

    int old_size = rcu_list.size;
    rcu_list.rcu_add(&rcu_list, *rcuinfo, rcu_list.size);
    if (old_size == rcu_list.size)
        return;

    gw_client_list.gw_client_add(&gw_client_list, sender, pkt->uidSrc, (u8 *) "", rcuinfo->IpAddr,
                  gw_client_list.size);

}

void set_dev_info(UDPPROPKT *pkt) {
    int dev_cnt = 0;
    switch (pkt->subType2) {
        case e_ware_airCond:
            dev_cnt = pkt->datLen / WARE_AIR_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_AIR_SIZE);

                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);

                DEV_PRO_AIRCOND *aircond = (DEV_PRO_AIRCOND *) ware_dev->dat;
                aircond_list.ware_aircond_add(&aircond_list, *ware_dev, *aircond, pkt->uidSrc,
                                 aircond_list.size);
            }
            break;

        case e_ware_tv:
            dev_cnt = pkt->datLen / WARE_TV_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_TV_SIZE);

                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);
                tv_list.ware_tv_add(&tv_list, *ware_dev, pkt->uidSrc, tv_list.size);
            }
            break;

        case e_ware_tvUP:
            dev_cnt = pkt->datLen / WARE_TVUP_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_TVUP_SIZE);

                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);
                tvUP_list.ware_tvUP_add(&tvUP_list, *ware_dev, pkt->uidSrc, tvUP_list.size);
            }
            break;

        case e_ware_light:
            dev_cnt = pkt->datLen / WARE_LGT_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_LGT_SIZE);
                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);

                DEV_PRO_LIGHT *light = (DEV_PRO_LIGHT *) ware_dev->dat;
                light_list.ware_light_add(&light_list, *ware_dev, *light, pkt->uidSrc, light_list.size);
            }
            break;

        case e_ware_curtain:
            dev_cnt = pkt->datLen / WARE_CUR_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_CUR_SIZE);

                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);

                DEV_PRO_CURTAIN *curtain = (DEV_PRO_CURTAIN *) ware_dev->dat;
                curtain_list.ware_curtain_add(&curtain_list, *ware_dev, *curtain, pkt->uidSrc,
                                 curtain_list.size);
            }
            break;
        case e_ware_lock:
            dev_cnt = pkt->datLen / WARE_LOCK_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_LOCK_SIZE);

                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);

                DEV_PRO_LOCK *lock = (DEV_PRO_LOCK *) ware_dev->dat;
                lock_list.ware_lock_add(&lock_list, *ware_dev, *lock, pkt->uidSrc,
                                              lock_list.size);
            }
            break;
        case e_ware_value:
            dev_cnt = pkt->datLen / WARE_VALUE_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_VALUE_SIZE);

                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);

                DEV_PRO_VALVE *valve = (DEV_PRO_VALVE *) ware_dev->dat;
                valve_list.ware_valve_add(&valve_list, *ware_dev, *valve, pkt->uidSrc,
                                        valve_list.size);
            }
            break;
        case e_ware_fresh_air:
            dev_cnt = pkt->datLen / WARE_FRAIR_SIZE;

            for (int i = 0; i < dev_cnt; i++) {
                WARE_DEV *ware_dev = (WARE_DEV *) (pkt->dat + i * WARE_FRAIR_SIZE);

                ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);

                DEV_PRO_FRESHAIR *frair = (DEV_PRO_FRESHAIR *) ware_dev->dat;
                frair_list.ware_frair_add(&frair_list, *ware_dev, *frair, pkt->uidSrc,
                                          frair_list.size);
            }
            break;
        default:
            break;
    }
}


int get(int num, int index) {
    return (num & (0x1 << index)) >> index;
}

void ctrl_dev_reply(UDPPROPKT *pkt) {
    CHNS_STATUS *status = (CHNS_STATUS *) pkt->dat;
    char bin_string[13];
    itoa_bin(status->state, bin_string);
    LOGI("通道状态:%s\n", bin_string);

    for (int i = 0; i < 12; i++) {
        Node_light *light_head = light_list.head;
        if (get(status->state, i) == 1) {
            for (int j = 0; j < light_list.size; j++, light_head = light_head->next) {
                if (memcmp(light_head->ware_dev.canCpuId, status->devUnitID, 12) == 0 &&
                    light_head->light.powChn == i) {
                    light_head->light.bOnOff = 1;
                }
            }
        } else {
            for (int j = 0; j < light_list.size; j++, light_head = light_head->next) {
                if (memcmp(light_head->ware_dev.canCpuId, status->devUnitID, 12) == 0 &&
                    light_head->light.powChn == i) {
                    light_head->light.bOnOff = 0;
                }
            }
        }
    }
}

void fresh_dev_info(UDPPROPKT *pkt) {
    WARE_DEV *ware_dev = (WARE_DEV *) pkt->dat;
    ware_list.ware_add(&ware_list, *ware_dev, pkt->uidSrc, ware_list.size);
    switch (ware_dev->devType) {
        case e_ware_airCond: {
            DEV_PRO_AIRCOND *aircond = (DEV_PRO_AIRCOND *) ware_dev->dat;
            aircond_list.ware_aircond_add(&aircond_list, *ware_dev, *aircond, pkt->uidSrc, aircond_list.size);
            break;
        }
        case e_ware_tv:
            tv_list.ware_tv_add(&tv_list, *ware_dev, pkt->uidSrc, tv_list.size);
            break;
        case e_ware_tvUP:
            tvUP_list.ware_tvUP_add(&tvUP_list, *ware_dev, pkt->uidSrc, tvUP_list.size);
            break;
        case e_ware_light: {
            DEV_PRO_LIGHT *light = (DEV_PRO_LIGHT *) ware_dev->dat;
            light_list.ware_light_add(&light_list, *ware_dev, *light, pkt->uidSrc, light_list.size); }
            break;
        case e_ware_curtain:{
            DEV_PRO_CURTAIN *curtain = (DEV_PRO_CURTAIN *)ware_dev->dat;
            curtain_list.ware_curtain_add(&curtain_list, *ware_dev, *curtain, pkt->uidSrc, curtain_list.size);
            break;
        }
        case e_ware_lock:{
            DEV_PRO_LOCK *lock = (DEV_PRO_LOCK *)ware_dev->dat;
            lock_list.ware_lock_add(&lock_list, *ware_dev, *lock, pkt->uidSrc, lock_list.size);
            break;
        }
        case e_ware_value:{
            DEV_PRO_VALVE *valve = (DEV_PRO_VALVE *)ware_dev->dat;
            valve_list.ware_valve_add(&valve_list, *ware_dev, *valve, pkt->uidSrc, valve_list.size);
            break;
        }
        case e_ware_fresh_air:{
            DEV_PRO_FRESHAIR *frair = (DEV_PRO_FRESHAIR *)ware_dev->dat;
            frair_list.ware_frair_add(&frair_list, *ware_dev, *frair, pkt->uidSrc, frair_list.size);
            break;
        }
        default:
            break;
    }
}

void del_dev_info(UDPPROPKT *pkt) {
    if (pkt->datLen > 255)
        return;

    WARE_DEV *ware_dev = (WARE_DEV *) pkt->dat;

    ware_list.ware_remove(&ware_list, *ware_dev, pkt->uidSrc);
    switch (ware_dev->devType) {
        case e_ware_airCond:
            aircond_list.ware_aircond_remove(&aircond_list, *ware_dev, pkt->uidSrc);
            break;
        case e_ware_tv:
            tv_list.ware_tv_remove(&tv_list, *ware_dev, pkt->uidSrc);
            break;
        case e_ware_tvUP:
            tvUP_list.ware_tvUP_remove(&tvUP_list, *ware_dev, pkt->uidSrc);
            break;
        case e_ware_light:
            light_list.ware_light_remove(&light_list, *ware_dev, pkt->uidSrc);
            break;
        case e_ware_curtain:
            curtain_list.ware_curtain_remove(&curtain_list, *ware_dev, pkt->uidSrc);
            break;
        case e_ware_lock:
            lock_list.ware_lock_remove(&lock_list, *ware_dev, pkt->uidSrc);
            break;
        case e_ware_value:
            valve_list.ware_valve_remove(&valve_list, *ware_dev, pkt->uidSrc);
            break;
        case e_ware_fresh_air:
            frair_list.ware_frair_remove(&frair_list, *ware_dev, pkt->uidSrc);
            break;
        default:
            break;
    }
}

void set_events_info(UDPPROPKT *pkt) {
    int event_cnt = pkt->datLen / sizeof(SCENE_EVENT);

    for (int i = 0; i < event_cnt; i++) {
        SCENE_EVENT *event = (SCENE_EVENT *) (pkt->dat + i * sizeof(SCENE_EVENT));
        scene_list.ware_scene_add(&scene_list, *event, pkt->uidSrc, scene_list.size);
    }
}

void del_scene_info(UDPPROPKT *pkt) {
    SCENE_EVENT *event = (SCENE_EVENT *) pkt->dat;

    scene_list.ware_scene_remove(&scene_list, *event, pkt->uidSrc);
}

void set_board_info(UDPPROPKT *pkt) {
    BOARD_CHNOUT *board = (BOARD_CHNOUT *) pkt->dat; //四种类型结构一样

    switch (board->boardType) {
        case e_board_chnOut: {
            BOARD_CHNOUT *board = (BOARD_CHNOUT *) pkt->dat;
            board_list.board_add(&board_list, *board, pkt->uidSrc, board_list.size);
        }
            break;

        case e_board_keyInput: {
            BOARD_KEYINPUT *keyinput = (BOARD_KEYINPUT *) pkt->dat;
            keyinput_list.keyinput_add(&keyinput_list, *keyinput, pkt->uidSrc, keyinput_list.size);
        }
            break;

        case e_board_wlessIR: {
            BOARD_WLESSIR *wlessir = (BOARD_WLESSIR *) pkt->dat;
        }
            break;

        case e_board_envDetect: {
            BOARD_ENVDETECT *envdetect = (BOARD_ENVDETECT *) pkt->dat;
        }
            break;
        default:
            break;
    }
}

void del_board_info(UDPPROPKT *pkt) {
    BOARD_CHNOUT *temp = (BOARD_CHNOUT *) pkt;

    switch (temp->boardType) {
        case e_board_chnOut: {
            BOARD_CHNOUT *board = (BOARD_CHNOUT *) pkt->dat;
            board_list.board_remove(&board_list, *board, pkt->uidDst);
        }
            break;

        case e_board_keyInput: {
            BOARD_KEYINPUT *keyinput = (BOARD_KEYINPUT *) pkt->dat;
            keyinput_list.keyinput_remove(&keyinput_list, *keyinput, pkt->uidDst);
        }
            break;

        case e_board_wlessIR: {
            BOARD_WLESSIR *wlessir = (BOARD_WLESSIR *) pkt->dat;
        }
            break;

        case e_board_envDetect: {
            BOARD_ENVDETECT *envdetect = (BOARD_ENVDETECT *) pkt->dat;
        }

        default:
            break;
    }
}

void set_key_opitem(UDPPROPKT *pkt) {
    int cnt = (pkt->datLen - 12) / sizeof(KEYOP_ITEM);

    u8 keyinput_board_id[12];
    memcpy(keyinput_board_id, pkt->dat, 12);

    for (int i = 0; i < cnt; i++) {
        KEYOP_ITEM *keyop_item = (KEYOP_ITEM *) (pkt->dat + 12 + sizeof(KEYOP_ITEM) * i);
        keyop_item_list.keyop_item_add(&keyop_item_list, *keyop_item, pkt->uidSrc, keyinput_board_id, pkt->subType2,
                       keyop_item_list.size);
    }
}

void del_key_opitem(UDPPROPKT *pkt) {
    u8 keyinput_board_id[12];
    memcpy(keyinput_board_id, pkt->dat, 12);

    keyop_item_list.keyop_item_remove(&keyop_item_list, pkt->uidSrc, keyinput_board_id, pkt->subType2);
}

void set_chn_opitem(UDPPROPKT *pkt) {
    int cnt = pkt->subType2;

    u8 chn_board_id[12];
    u8 devType, devID;

    memcpy(chn_board_id, pkt->dat, 12);
    devType = pkt->dat[12];
    devID = pkt->dat[13];

    chnop_item_list.chnop_item_clean(&chnop_item_list);
    for (int i = 0; i < cnt; i++) {
        CHNOP_ITEM *chnop_item = (CHNOP_ITEM *) (pkt->dat + 14 + sizeof(CHNOP_ITEM) * i);

        chnop_item_list.chnop_item_add(&chnop_item_list, *chnop_item, pkt->uidSrc, chn_board_id, devType, devID,
                       cnt, chnop_item_list.size);
    }
}

void del_chn_opitem(UDPPROPKT *pkt) {
    for (int i = 0; i < pkt->subType2; i++) {
        CHNOP_ITEM *item = (CHNOP_ITEM *) (pkt->dat + 14 + sizeof(CHNOP_ITEM) * i);

        chnop_item_list.chnop_item_remove(&chnop_item_list, *item, pkt->uidDst);
    }
}

