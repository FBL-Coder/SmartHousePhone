#ifndef DATA_PUSH_H
#define DATA_PUSH_H

#include "udp.h"
#include "udppropkt.h"

extern void get_broadcast_reply_json(UDPPROPKT *pkt);
extern void get_rcu_info_json(u8 *devUnitID, u8 *devPass, SOCKADDR_IN sender_client);
extern void report_rcu_info_json(UDPPROPKT *pkt);
extern void get_devs_info_json(u8 *devUnitID, SOCKADDR_IN sender_client);
extern void report_all_devs_info_json(u8 *devUnitID);
extern void get_events_info_json(u8 *devUnitID, SOCKADDR_IN sender_client);
extern void get_board_chnout_json(u8 *devUnitID, SOCKADDR_IN sender_client);
extern void get_board_keyinput_json(u8 *devUnitID, SOCKADDR_IN sender_client);
extern void report_all_board_keyinput_json(u8 *devUnitID);
extern void report_all_board_chnout_json(u8 *devUnitID);
extern void report_all_ctl_reply_json(UDPPROPKT *pkt);
extern void get_light_ctr_reply_json(UDPPROPKT *pkt);
extern void report_scene_ctl_reply_json(UDPPROPKT *pkt);
extern void get_chn_opitem_json(u8 *devUnitID, u8 *cpuCanID, int devType, int devID, SOCKADDR_IN sender_client);
extern void set_chn_opitem_reply_json(UDPPROPKT *pkt);
extern void report_key_opitem_json(u8 *devUnitID, u8 *cpuCanID, int key_index);
extern void report_chn_opitem_json(u8 *devUnitID, u8 *cpuCanID, int devType, int devID);
extern void set_key_opitem_reply_json(UDPPROPKT *pkt);

#endif // DATA_PUSH_H
