#include "json.h"

char *create_rcu_json(u8 *devUnitID, int datType, int subType1, int subType2)
{
    cJSON *root, *rcu_rows, *item;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(subType2));

    cJSON_AddItemToObject(root, "rcu_rows", rcu_rows = cJSON_CreateArray());

    node_rcu *rcu = rcu_list.head;
    for (int i = 0; i < rcu_list.size; i++, rcu = rcu->next) {
        if (memcmp(rcu->item.devUnitID, devUnitID, 12) == 0) {

            cJSON_AddItemToArray(rcu_rows, item = cJSON_CreateObject());
            u8 canCpuID[25] = {0};
            bytes_to_string(rcu->item.devUnitID, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            cJSON_AddItemToObject(item, "devUnitPass",
                                  cJSON_CreateString((char *) rcu->item.devUnitPass));

            char name[25] = {0};
            sprintf(name, "%x%x%x%x%x%x%x%x%x%x%x%x", rcu->item.name[0], rcu->item.name[1],
                    rcu->item.name[2], rcu->item.name[3], rcu->item.name[4], rcu->item.name[5],
                    rcu->item.name[6], rcu->item.name[7], rcu->item.name[8], rcu->item.name[9],
                    rcu->item.name[10], rcu->item.name[11]);
            cJSON_AddItemToObject(item, "name", cJSON_CreateString((char *)rcu->item.name));

            char ip[16] = {0};
            sprintf(ip, "%d.%d.%d.%d"
                    , rcu->item.IpAddr[0] & 0xff, rcu->item.IpAddr[1] & 0xff,
                    rcu->item.IpAddr[2] & 0xff, rcu->item.IpAddr[3] & 0xff);
            cJSON_AddItemToObject(item, "IpAddr", cJSON_CreateString(ip));

            char subMask[16] = {0};
            sprintf(subMask, "%d.%d.%d.%d", rcu->item.SubMask[0] & 0xff,
                    rcu->item.SubMask[1] & 0xff, rcu->item.SubMask[2] & 0xff,
                    rcu->item.SubMask[3] & 0xff);
            cJSON_AddItemToObject(item, "SubMask", cJSON_CreateString(subMask));

            char gateway[16] = {0};
            sprintf(gateway, "%d.%d.%d.%d", rcu->item.Gateway[0] & 0xff,
                    rcu->item.Gateway[1] & 0xff, rcu->item.Gateway[2] & 0xff,
                    rcu->item.Gateway[3] & 0xff);
            cJSON_AddItemToObject(item, "Gateway", cJSON_CreateString(gateway));

            char centerServ[16] = {0};
            sprintf(centerServ, "%d.%d.%d.%d", rcu->item.centerServ[0] & 0xff,
                    rcu->item.centerServ[1] & 0xff, rcu->item.centerServ[2] & 0xff,
                    rcu->item.centerServ[3] & 0xff);
            cJSON_AddItemToObject(item, "centerServ", cJSON_CreateString(centerServ));

            char roomNum[9] = {0};
            sprintf(roomNum, "%d%d%d%d", rcu->item.roomNum[0] & 0xff, rcu->item.roomNum[1] & 0xff,
                    rcu->item.roomNum[2] & 0xff, rcu->item.roomNum[3] & 0xff);
            cJSON_AddItemToObject(item, "roomNum", cJSON_CreateString(roomNum));

            char macAddr[13] = {0};
            sprintf(macAddr, "%02x%02x%02x%02x%02x%02x", rcu->item.macAddr[0] & 0xff,
                    rcu->item.macAddr[1] & 0xff, rcu->item.macAddr[2] & 0xff,
                    rcu->item.macAddr[3] & 0xff, rcu->item.macAddr[4] & 0xff,
                    rcu->item.macAddr[5] & 0xff);
            cJSON_AddItemToObject(item, "macAddr", cJSON_CreateString(macAddr));

            cJSON_AddItemToObject(item, "SoftVersion",
                                  cJSON_CreateString((char *) rcu->item.SoftVersion));
            cJSON_AddItemToObject(item, "HwVersion",
                                  cJSON_CreateString((char *) rcu->item.HwVersion));
            cJSON_AddItemToObject(item, "bDhcp", cJSON_CreateNumber(rcu->item.bDhcp));
        }
    }
    return cJSON_Print(root);
}

//针对灯光和新风设备的控制返回状态
char *create_chn_status_json(u8 *devUnitID, int datType, int subType1, int subType2)
{
    cJSON *root, *light_rows, *item;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(subType2));

    cJSON_AddItemToObject(root, "light_rows", light_rows = cJSON_CreateArray());

    int item_num = 0;
    Node_light *light = light_list.head;
    for (int i = 0; i < light_list.size; i++, light = light->next) {
        if (memcmp(light->devUnitID, devUnitID, 12) == 0) {

            item_num++;

            cJSON_AddItemToArray(light_rows, item = cJSON_CreateObject());
            u8 canCpuID[25] = {0};
            bytes_to_string(light->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));
            //u8 devName[25] = {0};
            //bytes_to_string(light->ware_dev.devName, devName, 12);
            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", light->ware_dev.devName[0],
                    light->ware_dev.devName[1], light->ware_dev.devName[2],
                    light->ware_dev.devName[3], light->ware_dev.devName[4],
                    light->ware_dev.devName[5], light->ware_dev.devName[6],
                    light->ware_dev.devName[7], light->ware_dev.devName[8],
                    light->ware_dev.devName[9], light->ware_dev.devName[10],
                    light->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString(devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", light->ware_dev.roomName[0],
                    light->ware_dev.roomName[1], light->ware_dev.roomName[2],
                    light->ware_dev.roomName[3], light->ware_dev.roomName[4],
                    light->ware_dev.roomName[5], light->ware_dev.roomName[6],
                    light->ware_dev.roomName[7], light->ware_dev.roomName[8],
                    light->ware_dev.roomName[9], light->ware_dev.roomName[10],
                    light->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString(roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(
                    light->ware_dev.devType ? light->ware_dev.devType : -1));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(
                    light->ware_dev.devId ? light->ware_dev.devId : -1));

            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(light->light.bOnOff));
            cJSON_AddItemToObject(item, "bTuneEn", cJSON_CreateNumber(light->light.bTuneEn));
            cJSON_AddItemToObject(item, "lmVal", cJSON_CreateNumber(light->light.lmVal));
            cJSON_AddItemToObject(item, "powChn", cJSON_CreateNumber(light->light.powChn));
        }
    }

    cJSON_AddNumberToObject(root, "light", item_num);

    return cJSON_Print(root);
}

char *create_dev_json(u8 *devUnitID, int datType, int subType1, int subType2)
{
    cJSON *root, *air_rows, *light_rows;
    cJSON *cur_rows, *tv_rows, *tvUP_rows;
    cJSON *lock_rows, *valve_rows, *frair_rows, *item;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(subType2));


    cJSON_AddItemToObject(root, "aircond_rows", air_rows = cJSON_CreateArray());

    int item_num = 0;
    Node_aircond *aircond = aircond_list.head;
    for (int i = 0; i < aircond_list.size; i++, aircond = aircond->next) {
        if (memcmp(aircond->devUnitID, devUnitID, 12) == 0) {
            item_num++;

            cJSON_AddItemToArray(air_rows, item = cJSON_CreateObject());
            u8 canCpuID[25] = {0};
            bytes_to_string(aircond->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", aircond->ware_dev.devName[0],
                    aircond->ware_dev.devName[1], aircond->ware_dev.devName[2],
                    aircond->ware_dev.devName[3], aircond->ware_dev.devName[4],
                    aircond->ware_dev.devName[5], aircond->ware_dev.devName[6],
                    aircond->ware_dev.devName[7], aircond->ware_dev.devName[8],
                    aircond->ware_dev.devName[9], aircond->ware_dev.devName[10],
                    aircond->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString(devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", aircond->ware_dev.roomName[0],
                    aircond->ware_dev.roomName[1], aircond->ware_dev.roomName[2],
                    aircond->ware_dev.roomName[3], aircond->ware_dev.roomName[4],
                    aircond->ware_dev.roomName[5], aircond->ware_dev.roomName[6],
                    aircond->ware_dev.roomName[7], aircond->ware_dev.roomName[8],
                    aircond->ware_dev.roomName[9], aircond->ware_dev.roomName[10],
                    aircond->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString(roomName));

            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(aircond->ware_dev.devType));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(aircond->ware_dev.devId));

            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(aircond->aircond.bOnOff));
            cJSON_AddItemToObject(item, "selMode", cJSON_CreateNumber(aircond->aircond.selMode));
            cJSON_AddItemToObject(item, "selTemp", cJSON_CreateNumber(aircond->aircond.selTemp));
            cJSON_AddItemToObject(item, "selSpd", cJSON_CreateNumber(aircond->aircond.selSpd));
            cJSON_AddItemToObject(item, "selDirect",
                                  cJSON_CreateNumber(aircond->aircond.selDirect));
            cJSON_AddItemToObject(item, "rev1", cJSON_CreateNumber(aircond->aircond.rev1));
            cJSON_AddItemToObject(item, "powChn", cJSON_CreateNumber(aircond->aircond.powChn));
        }
    }
    cJSON_AddItemToObject(root, "aircond", cJSON_CreateNumber(item_num));
    item_num = 0;

    cJSON_AddItemToObject(root, "light_rows", light_rows = cJSON_CreateArray());

    Node_light *light = light_list.head;
    for (int i = 0; i < light_list.size; i++, light = light->next) {
        if (memcmp(light->devUnitID, devUnitID, 12) == 0) {
            item_num++;

            cJSON_AddItemToArray(light_rows, item = cJSON_CreateObject());
            u8 canCpuID[25] = {0};
            bytes_to_string(light->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));
            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", light->ware_dev.devName[0],
                    light->ware_dev.devName[1], light->ware_dev.devName[2],
                    light->ware_dev.devName[3], light->ware_dev.devName[4],
                    light->ware_dev.devName[5], light->ware_dev.devName[6],
                    light->ware_dev.devName[7], light->ware_dev.devName[8],
                    light->ware_dev.devName[9], light->ware_dev.devName[10],
                    light->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString(devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", light->ware_dev.roomName[0],
                    light->ware_dev.roomName[1], light->ware_dev.roomName[2],
                    light->ware_dev.roomName[3], light->ware_dev.roomName[4],
                    light->ware_dev.roomName[5], light->ware_dev.roomName[6],
                    light->ware_dev.roomName[7], light->ware_dev.roomName[8],
                    light->ware_dev.roomName[9], light->ware_dev.roomName[10],
                    light->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString(roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(
                    light->ware_dev.devType ? light->ware_dev.devType : -1));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(
                    light->ware_dev.devId ? light->ware_dev.devId : -1));

            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(light->light.bOnOff));
            cJSON_AddItemToObject(item, "bTuneEn", cJSON_CreateNumber(light->light.bTuneEn));
            cJSON_AddItemToObject(item, "lmVal", cJSON_CreateNumber(light->light.lmVal));
            cJSON_AddItemToObject(item, "powChn", cJSON_CreateNumber(light->light.powChn));
        }
    }
    cJSON_AddItemToObject(root, "light", cJSON_CreateNumber(item_num));
    item_num = 0;

    cJSON_AddItemToObject(root, "curtain_rows", cur_rows = cJSON_CreateArray());
    node_curtain *curtain = curtain_list.head;
    for (int i = 0; i < curtain_list.size; i++, curtain = curtain->next) {
        if (memcmp(curtain->devUnitID, devUnitID, 12) == 0) {

            item_num++;

            cJSON_AddItemToArray(cur_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(curtain->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", curtain->ware_dev.devName[0],
                    curtain->ware_dev.devName[1], curtain->ware_dev.devName[2],
                    curtain->ware_dev.devName[3], curtain->ware_dev.devName[4],
                    curtain->ware_dev.devName[5], curtain->ware_dev.devName[6],
                    curtain->ware_dev.devName[7], curtain->ware_dev.devName[8],
                    curtain->ware_dev.devName[9], curtain->ware_dev.devName[10],
                    curtain->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString((char *) devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", curtain->ware_dev.roomName[0],
                    curtain->ware_dev.roomName[1], curtain->ware_dev.roomName[2],
                    curtain->ware_dev.roomName[3], curtain->ware_dev.roomName[4],
                    curtain->ware_dev.roomName[5], curtain->ware_dev.roomName[6],
                    curtain->ware_dev.roomName[7], curtain->ware_dev.roomName[8],
                    curtain->ware_dev.roomName[9], curtain->ware_dev.roomName[10],
                    curtain->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString((char *) roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(curtain->ware_dev.devType));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(curtain->ware_dev.devId));

            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(curtain->curtain.bOnOff));
            cJSON_AddItemToObject(item, "timRun", cJSON_CreateNumber(curtain->curtain.timRun));
            cJSON_AddItemToObject(item, "powChn", cJSON_CreateNumber(curtain->curtain.powChn));
        }
    }
    cJSON_AddItemToObject(root, "curtain", cJSON_CreateNumber(item_num));

    cJSON_AddItemToObject(root, "lock_rows", lock_rows = cJSON_CreateArray());
    node_lock *lock = lock_list.head;
    for (int i = 0; i < lock_list.size; i++, lock = lock->next) {
        if (memcmp(lock->devUnitID, devUnitID, 12) == 0) {

            item_num++;

            cJSON_AddItemToArray(lock_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(lock->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", lock->ware_dev.devName[0],
                    lock->ware_dev.devName[1], lock->ware_dev.devName[2],
                    lock->ware_dev.devName[3], lock->ware_dev.devName[4],
                    lock->ware_dev.devName[5], lock->ware_dev.devName[6],
                    lock->ware_dev.devName[7], lock->ware_dev.devName[8],
                    lock->ware_dev.devName[9], lock->ware_dev.devName[10],
                    lock->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString((char *) devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", lock->ware_dev.roomName[0],
                    lock->ware_dev.roomName[1], lock->ware_dev.roomName[2],
                    lock->ware_dev.roomName[3], lock->ware_dev.roomName[4],
                    lock->ware_dev.roomName[5], lock->ware_dev.roomName[6],
                    lock->ware_dev.roomName[7], lock->ware_dev.roomName[8],
                    lock->ware_dev.roomName[9], lock->ware_dev.roomName[10],
                    lock->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString((char *) roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(lock->ware_dev.devType));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(lock->ware_dev.devId));

            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(lock->lock.bOnOff));
            cJSON_AddItemToObject(item, "timRun", cJSON_CreateNumber(lock->lock.timRun));
            cJSON_AddItemToObject(item, "bLockOut", cJSON_CreateNumber(lock->lock.bLockOut));
            cJSON_AddItemToObject(item, "powChnOpen", cJSON_CreateNumber(lock->lock.powChnOpen));
            cJSON_AddItemToObject(item, "pwd", cJSON_CreateString((char *)lock->lock.pwd));
        }
    }
    cJSON_AddItemToObject(root, "lock", cJSON_CreateNumber(item_num));

    cJSON_AddItemToObject(root, "valve_rows", valve_rows = cJSON_CreateArray());
    node_valve *valve = valve_list.head;
    for (int i = 0; i < valve_list.size; i++, valve = valve->next) {
        if (memcmp(valve->devUnitID, devUnitID, 12) == 0) {
            item_num++;
            cJSON_AddItemToArray(valve_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(valve->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", valve->ware_dev.devName[0],
                    valve->ware_dev.devName[1], valve->ware_dev.devName[2],
                    valve->ware_dev.devName[3], valve->ware_dev.devName[4],
                    valve->ware_dev.devName[5], valve->ware_dev.devName[6],
                    valve->ware_dev.devName[7], valve->ware_dev.devName[8],
                    valve->ware_dev.devName[9], valve->ware_dev.devName[10],
                    valve->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString((char *) devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", valve->ware_dev.roomName[0],
                    valve->ware_dev.roomName[1], valve->ware_dev.roomName[2],
                    valve->ware_dev.roomName[3], valve->ware_dev.roomName[4],
                    valve->ware_dev.roomName[5], valve->ware_dev.roomName[6],
                    valve->ware_dev.roomName[7], valve->ware_dev.roomName[8],
                    valve->ware_dev.roomName[9], valve->ware_dev.roomName[10],
                    valve->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString((char *) roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(valve->ware_dev.devType));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(valve->ware_dev.devId));

            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(valve->valve.bOnOff));
            cJSON_AddItemToObject(item, "timRun", cJSON_CreateNumber(valve->valve.timRun));
            cJSON_AddItemToObject(item, "powChnOpen", cJSON_CreateNumber(valve->valve.powChnOpen));
            cJSON_AddItemToObject(item, "powChnClose", cJSON_CreateNumber(valve->valve.powChnClose));
        }
    }
    cJSON_AddItemToObject(root, "valve", cJSON_CreateNumber(item_num));

    cJSON_AddItemToObject(root, "frair_rows", frair_rows = cJSON_CreateArray());
    node_frair *frair = frair_list.head;
    for (int i = 0; i < frair_list.size; i++, frair = frair->next) {
        if (memcmp(frair->devUnitID, devUnitID, 12) == 0) {
            item_num++;
            cJSON_AddItemToArray(frair_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(frair->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", frair->ware_dev.devName[0],
                    frair->ware_dev.devName[1], frair->ware_dev.devName[2],
                    frair->ware_dev.devName[3], frair->ware_dev.devName[4],
                    frair->ware_dev.devName[5], frair->ware_dev.devName[6],
                    frair->ware_dev.devName[7], frair->ware_dev.devName[8],
                    frair->ware_dev.devName[9], frair->ware_dev.devName[10],
                    frair->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString((char *) devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", frair->ware_dev.roomName[0],
                    frair->ware_dev.roomName[1], frair->ware_dev.roomName[2],
                    frair->ware_dev.roomName[3], frair->ware_dev.roomName[4],
                    frair->ware_dev.roomName[5], frair->ware_dev.roomName[6],
                    frair->ware_dev.roomName[7], frair->ware_dev.roomName[8],
                    frair->ware_dev.roomName[9], frair->ware_dev.roomName[10],
                    frair->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString((char *) roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(frair->ware_dev.devType));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(frair->ware_dev.devId));

            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(frair->frair.bOnOff));
            cJSON_AddItemToObject(item, "spdSel", cJSON_CreateNumber(frair->frair.spdSel));
            cJSON_AddItemToObject(item, "powChn", cJSON_CreateNumber(frair->frair.powChn));
        }
    }
    cJSON_AddItemToObject(root, "frair", cJSON_CreateNumber(item_num));

    cJSON_AddItemToObject(root, "tv_rows", tv_rows = cJSON_CreateArray());
    node_tv *tv = tv_list.head;
    for (int i = 0; i < tv_list.size; i++, tv = tv->next) {
        if (memcmp(tv->devUnitID, devUnitID, 12) == 0) {

            item_num++;

            cJSON_AddItemToArray(tv_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(curtain->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", tv->ware_dev.devName[0],
                    tv->ware_dev.devName[1], tv->ware_dev.devName[2],
                    tv->ware_dev.devName[3], tv->ware_dev.devName[4],
                    tv->ware_dev.devName[5], tv->ware_dev.devName[6],
                    tv->ware_dev.devName[7], tv->ware_dev.devName[8],
                    tv->ware_dev.devName[9], tv->ware_dev.devName[10],
                    tv->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString((char *) devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", tv->ware_dev.roomName[0],
                    tv->ware_dev.roomName[1], tv->ware_dev.roomName[2],
                    tv->ware_dev.roomName[3], tv->ware_dev.roomName[4],
                    tv->ware_dev.roomName[5], tv->ware_dev.roomName[6],
                    tv->ware_dev.roomName[7], tv->ware_dev.roomName[8],
                    tv->ware_dev.roomName[9], tv->ware_dev.roomName[10],
                    tv->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString((char *) roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(tv->ware_dev.devType));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(tv->ware_dev.devId));
        }
    }
    cJSON_AddItemToObject(root, "tv", cJSON_CreateNumber(item_num));

    cJSON_AddItemToObject(root, "tvUP_rows", tvUP_rows = cJSON_CreateArray());
    node_tvUP *tvUP = tvUP_list.head;
    for (int i = 0; i < tvUP_list.size; i++, tvUP = tvUP->next) {
        if (memcmp(tvUP->devUnitID, devUnitID, 12) == 0) {

            item_num++;

            cJSON_AddItemToArray(tvUP_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(curtain->ware_dev.canCpuId, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char devName[25] = {0};
            sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", tvUP->ware_dev.devName[0],
                    tvUP->ware_dev.devName[1], tvUP->ware_dev.devName[2],
                    tvUP->ware_dev.devName[3], tvUP->ware_dev.devName[4],
                    tvUP->ware_dev.devName[5], tvUP->ware_dev.devName[6],
                    tvUP->ware_dev.devName[7], tvUP->ware_dev.devName[8],
                    tvUP->ware_dev.devName[9], tvUP->ware_dev.devName[10],
                    tvUP->ware_dev.devName[11]);
            cJSON_AddItemToObject(item, "devName", cJSON_CreateString((char *) devName));
            char roomName[25] = {0};
            //bytes_to_string(light->ware_dev.roomName, roomName, 12);
            sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", tvUP->ware_dev.roomName[0],
                    tvUP->ware_dev.roomName[1], tvUP->ware_dev.roomName[2],
                    tvUP->ware_dev.roomName[3], tvUP->ware_dev.roomName[4],
                    tvUP->ware_dev.roomName[5], tvUP->ware_dev.roomName[6],
                    tvUP->ware_dev.roomName[7], tvUP->ware_dev.roomName[8],
                    tvUP->ware_dev.roomName[9], tvUP->ware_dev.roomName[10],
                    tvUP->ware_dev.roomName[11]);
            cJSON_AddItemToObject(item, "roomName", cJSON_CreateString((char *) roomName));
            cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(tvUP->ware_dev.devType));
            cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(tvUP->ware_dev.devId));
        }
    }
    cJSON_AddItemToObject(root, "tvUP", cJSON_CreateNumber(item_num));

    return cJSON_Print(root);
}

char *create_events_json(u8 *devUnitID, int datType, int subType1, int subType2)
{
    cJSON *root, *scene_rows, *item, *item2, *itemAry;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(subType2));

    // 在object中加入array
    cJSON_AddItemToObject(root, "scene_rows", scene_rows = cJSON_CreateArray());

    int item_num = 0;
    node_scene *scene = scene_list.head;
    for (int i = 0; i < scene_list.size; i++, scene = scene->next) {
        if (memcmp(scene->devUnitID, devUnitID, 12) == 0) {
            item_num++;

            cJSON_AddItemToArray(scene_rows, item = cJSON_CreateObject());
            char sceneName[25] = {0};
            sprintf(sceneName, "%x%x%x%x%x%x%x%x", scene->scene.sceneName[0],
                    scene->scene.sceneName[1], scene->scene.sceneName[2], scene->scene.sceneName[3],
                    scene->scene.sceneName[4], scene->scene.sceneName[5], scene->scene.sceneName[6],
                    scene->scene.sceneName[7]);

            cJSON_AddItemToObject(item, "sceneName", cJSON_CreateString(sceneName));
            cJSON_AddItemToObject(item, "devCnt", cJSON_CreateNumber(scene->scene.devCnt));
            cJSON_AddItemToObject(item, "eventID", cJSON_CreateNumber(scene->scene.eventId));

            cJSON_AddItemToObject(item, "itemAry", itemAry = cJSON_CreateArray());
            for (int j = 0; j < scene->scene.devCnt; j++) {
                cJSON_AddItemToArray(itemAry, item2 = cJSON_CreateObject());

                RUN_DEV_ITEM run_dev_item = (RUN_DEV_ITEM) scene->scene.itemAry[j];

                u8 canCpuID[25] = {0};
                bytes_to_string(run_dev_item.uid, canCpuID, 12);
                cJSON_AddItemToObject(item2, "canCpuID", cJSON_CreateString((char *) canCpuID));

                cJSON_AddItemToObject(item2, "devID", cJSON_CreateNumber(run_dev_item.devID));
                cJSON_AddItemToObject(item2, "devType", cJSON_CreateNumber(run_dev_item.devType));

                cJSON_AddItemToObject(item2, "lmVal", cJSON_CreateNumber(run_dev_item.lmVal));
                cJSON_AddItemToObject(item2, "rev2", cJSON_CreateNumber(run_dev_item.rev2));
                cJSON_AddItemToObject(item2, "rev3", cJSON_CreateNumber(run_dev_item.rev3));
                cJSON_AddItemToObject(item2, "bOnOff", cJSON_CreateNumber(run_dev_item.bOnoff));
                cJSON_AddItemToObject(item2, "param1", cJSON_CreateNumber(run_dev_item.param1));
                cJSON_AddItemToObject(item2, "param2", cJSON_CreateNumber(run_dev_item.param2));
            }
        }
    }

    cJSON_AddItemToObject(root, "scene", cJSON_CreateNumber(item_num));

    return cJSON_Print(root);
}

char *create_board_chnout_json(u8 *devUnitID, int datType, int subType1, int subType2)
{
    cJSON *root, *chnout_rows, *chnName_rows, *item;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    int item_num = 0;
    node_board *board = board_list.head;

    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(subType2));


    cJSON_AddItemToObject(root, "chnout_rows", chnout_rows = cJSON_CreateArray());

    for (int i = 0; i < board_list.size; i++, board = board->next) {
        if (memcmp(board->devUnitID, devUnitID, 12) == 0) {
            item_num++;
            cJSON_AddItemToArray(chnout_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(board->board.devUnitID, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char boardName[25] = {0};
            sprintf(boardName, "%x%x%x%x%x%x%x%x%x%x%x%x", board->board.boardName[0],
                    board->board.boardName[1], board->board.boardName[2], board->board.boardName[3],
                    board->board.boardName[4], board->board.boardName[5], board->board.boardName[6],
                    board->board.boardName[7], board->board.boardName[8], board->board.boardName[9],
                    board->board.boardName[10], board->board.boardName[11]);

            cJSON_AddItemToObject(item, "boardName", cJSON_CreateString(boardName));
            cJSON_AddItemToObject(item, "boardType", cJSON_CreateNumber(board->board.boardType));
            cJSON_AddItemToObject(item, "chnCnt", cJSON_CreateNumber(board->board.chnCnt));
            cJSON_AddItemToObject(item, "bOnline", cJSON_CreateNumber(board->board.bOnline));

            cJSON_AddItemToObject(item, "chnName_rows", chnName_rows = cJSON_CreateArray());
            for (int j = 0; j < board->board.chnCnt; j++) {
                char chnName[25] = {0};
                //bytes_to_string(board->board.chnName[j], chnName, 12);
                sprintf(chnName, "%x%x%x%x%x%x%x%x%x%x%x%x", board->board.chnName[j][0],
                        board->board.chnName[j][1], board->board.chnName[j][2],
                        board->board.chnName[j][3], board->board.chnName[j][4],
                        board->board.chnName[j][5], board->board.chnName[j][6],
                        board->board.chnName[j][7], board->board.chnName[j][8],
                        board->board.chnName[j][9], board->board.chnName[j][10],
                        board->board.chnName[j][11]);

                cJSON_AddItemToObject(chnName_rows, "chnName", cJSON_CreateString(chnName));
            }
        }
    }

    cJSON_AddItemToObject(root, "board", cJSON_CreateNumber(board_list.size));

    return cJSON_Print(root);
}

char *create_board_keyinput_json(u8 *devUnitID, int datType, int subType1, int subType2)
{
    cJSON *root, *keyinput_rows, *keyName_rows, *keyAllCtrlType_rows, *item;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    int item_num = 0;
    node_keyinput *keyinput = keyinput_list.head;

    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(subType2));


    cJSON_AddItemToObject(root, "keyinput_rows", keyinput_rows = cJSON_CreateArray());

    for (int i = 0; i < keyinput_list.size; i++, keyinput = keyinput->next) {
        if (memcmp(keyinput->devUnitID, devUnitID, 12) == 0) {
            item_num++;
            cJSON_AddItemToArray(keyinput_rows, item = cJSON_CreateObject());

            u8 canCpuID[25] = {0};
            bytes_to_string(keyinput->keyinput.devUnitID, canCpuID, 12);
            cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

            char boardName[25] = {0};
            sprintf(boardName, "%x%x%x%x%x%x%x%x%x%x%x%x", keyinput->keyinput.boardName[0],
                    keyinput->keyinput.boardName[1], keyinput->keyinput.boardName[2],
                    keyinput->keyinput.boardName[3], keyinput->keyinput.boardName[4],
                    keyinput->keyinput.boardName[5], keyinput->keyinput.boardName[6],
                    keyinput->keyinput.boardName[7], keyinput->keyinput.boardName[8],
                    keyinput->keyinput.boardName[9], keyinput->keyinput.boardName[10],
                    keyinput->keyinput.boardName[11]);

            cJSON_AddItemToObject(item, "boardName", cJSON_CreateString(boardName));
            cJSON_AddItemToObject(item, "boardType",
                                  cJSON_CreateNumber(keyinput->keyinput.boardType));
            cJSON_AddItemToObject(item, "keyCnt", cJSON_CreateNumber(keyinput->keyinput.keyCnt));
            cJSON_AddItemToObject(item, "ledBkType",
                                  cJSON_CreateNumber(keyinput->keyinput.ledBkType));

            cJSON_AddItemToObject(item, "keyName_rows", keyName_rows = cJSON_CreateArray());
            for (int j = 0; j < 6; j++) {
                char keyName[25] = {0};
                sprintf(keyName, "%x%x%x%x%x%x%x%x%x%x%x%x", keyinput->keyinput.keyName[j][0],
                        keyinput->keyinput.keyName[j][1], keyinput->keyinput.keyName[j][2],
                        keyinput->keyinput.keyName[j][3], keyinput->keyinput.keyName[j][4],
                        keyinput->keyinput.keyName[j][5], keyinput->keyinput.keyName[j][6],
                        keyinput->keyinput.keyName[j][7], keyinput->keyinput.keyName[j][8],
                        keyinput->keyinput.keyName[j][9], keyinput->keyinput.keyName[j][10],
                        keyinput->keyinput.keyName[j][11]);

                cJSON_AddItemToObject(keyName_rows, "keyName", cJSON_CreateString(keyName));
            }

            cJSON_AddItemToObject(item, "keyAllCtrlType_rows",
                                  keyAllCtrlType_rows = cJSON_CreateArray());
            for (int k = 0; k < 6; k++) {
                cJSON_AddItemToObject(keyAllCtrlType_rows, "keyAllCtrlType",
                                      cJSON_CreateNumber(keyinput->keyinput.keyAllCtrlType[k]));
            }
        }
    }
    cJSON_AddItemToObject(root, "keyinput", cJSON_CreateNumber(keyinput_list.size));

    return cJSON_Print(root);
}

void set_rcuInfo_json(u8 *devUnitID, u8 *devUnitPass, u8 *name, u8 *IpAddr, u8 *SubMask, u8 *Gateway,
                      u8 *centerServ, u8 *roomNum, u8 *macAddr, int bDhcp){

    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);

            u8 data_buf[RCU_INFO_SIZE];
            memset(data_buf, 0, RCU_INFO_SIZE);
            RCU_INFO rcu;
            memcpy(rcu.devUnitID, devUnitID, 12);
            memcpy(rcu.devUnitPass, devUnitPass, 8);
            memcpy(rcu.name, name, 12);
            memcpy(rcu.IpAddr, IpAddr, 4);
            memcpy(rcu.SubMask, SubMask, 4);
            memcpy(rcu.Gateway, Gateway, 4);
            memcpy(rcu.centerServ, centerServ, 4);
            memcpy(rcu.roomNum, roomNum, 4);
            memcpy(rcu.macAddr, macAddr, 6);
            rcu.bDhcp = bDhcp;

            memcpy(data_buf, &rcu, RCU_INFO_SIZE);

            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                   RCU_INFO_SIZE, e_udpPro_setRcuInfo, gw->gw_id, gw->gw_pass,
                                                   IS_ACK, 0, 0);
            int len = UDP_PKT_SIZE + RCU_INFO_SIZE;
            sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                   sizeof(gw->gw_sender));

        }
    }
}

void add_devs_json(u8 *devUnitID, u8 *canCpuID, int datType, int devType, int powChn, u8 *roomName, u8 *devName)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);
            //canCpuID, devid, devtype, ctrl_cmd, statue
            WARE_DEV ware_dev;
            memcpy(ware_dev.canCpuId, canCpuID, 12);
            ware_dev.devType = devType;
            memcpy(ware_dev.roomName, roomName, 12);
            memcpy(ware_dev.devName, devName, 12);

            switch (devType) {
                case e_ware_airCond: {
                    u8 data_buf[WARE_AIR_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);
                    DEV_PRO_AIRCOND air;
                    air.powChn = powChn;
                    memcpy(data_buf + WARE_DEV_SIZE, &air, AIR_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_AIR_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_AIR_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                }
                case e_ware_tv: {
                    u8 data_buf[WARE_TV_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_TV_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_TV_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                }
                case e_ware_tvUP: {
                    u8 data_buf[WARE_TVUP_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_TVUP_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_TVUP_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                }
                    break;
                case e_ware_light: {
                    u8 data_buf[WARE_LGT_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);
                    DEV_PRO_LIGHT light;
                    light.powChn = powChn;
                    memcpy(data_buf + WARE_DEV_SIZE, &light, LIGHT_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_LGT_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_LGT_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));
                }
                    break;
                case e_ware_curtain: {
                    u8 data_buf[WARE_CUR_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);
                    DEV_PRO_CURTAIN curtain;
                    curtain.powChn = powChn;
                    memcpy(data_buf + WARE_DEV_SIZE, &curtain, CURTAIN_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_CUR_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_CUR_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));
                }
                case e_ware_lock: {
                    u8 data_buf[WARE_LOCK_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);
                    DEV_PRO_LOCK lock;
                    lock.powChnOpen = powChn;
                    memcpy(data_buf + WARE_DEV_SIZE, &lock, LOCK_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_LOCK_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_LOCK_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));
                }
                case e_ware_value: {
                    u8 data_buf[WARE_VALUE_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);
                    DEV_PRO_LOCK lock;
                    lock.powChnOpen = powChn;
                    memcpy(data_buf + WARE_DEV_SIZE, &lock, VALUE_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_VALUE_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_VALUE_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));
                }
                case e_ware_fresh_air: {
                    u8 data_buf[WARE_FRAIR_SIZE];
                    memcpy(data_buf, &ware_dev, WARE_DEV_SIZE);
                    DEV_PRO_LOCK lock;
                    lock.powChnOpen = powChn;
                    memcpy(data_buf + WARE_DEV_SIZE, &lock, FRAIR_SIZE);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                           data_buf, WARE_FRAIR_SIZE, \
                                                       datType, gw->gw_id, gw->gw_pass,
                                                           IS_ACK, 0, 0);
                    int len = UDP_PKT_SIZE + WARE_FRAIR_SIZE;
                    sendto(primary_udp, send_pkt, len, 0,
                           (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));
                }
                default:
                    break;
            }
        }
    }
}

void ctrl_devs_json(u8 *devUnitID, u8 *canCpuID, int datType, int devType, int devID, int powChn, u8 *devName, u8 *roomName, int cmd)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);
            //canCpuID, devid, devtype, ctrl_cmd, statue
            int value = -1;
            if (datType == e_udpPro_ctrlDev)
                value = cmd;
            else if (datType == e_udpPro_editDev || datType == e_udpPro_delDev)
                value = devType;

            switch (devType) {
                case e_ware_airCond: {
                    Node_aircond *air = aircond_list.head;
                    for (int j = 0; j < aircond_list.size; j++, air = air->next) {
                        if (memcmp(air->ware_dev.canCpuId, canCpuID, 12) == 0
                            && air->ware_dev.devType == devType
                            && air->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(air->ware_dev.devName, devName, 12);
                                memcpy(air->ware_dev.roomName, roomName, 12);
                                air->aircond.powChn = powChn;
                            }
                            u8 data_buf[WARE_AIR_SIZE];
                            memcpy(data_buf, &air->ware_dev, WARE_DEV_SIZE);
                            memcpy(data_buf + WARE_DEV_SIZE, &air->aircond, AIR_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_AIR_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_AIR_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                        }
                    }
                }
                    break;
                case e_ware_light: {
                    Node_light *light = light_list.head;
                    for (int j = 0; j < light_list.size; j++, light = light->next) {
                        if (memcmp(light->ware_dev.canCpuId, canCpuID, 12) == 0
                            && light->ware_dev.devType == devType
                            && light->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(light->ware_dev.devName, devName, 12);
                                memcpy(light->ware_dev.roomName, roomName, 12);
                                light->light.powChn = powChn;
                            }
                            u8 data_buf[WARE_LGT_SIZE];
                            memcpy(data_buf, &light->ware_dev, WARE_DEV_SIZE);
                            memcpy(data_buf + WARE_DEV_SIZE, &light->light, LIGHT_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_LGT_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_LGT_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));
                        }
                    }
                }
                    break;
                case e_ware_curtain: {
                    node_curtain *curtain = curtain_list.head;
                    for (int j = 0; j < curtain_list.size; j++, curtain = curtain->next) {
                        if (memcmp(curtain->ware_dev.canCpuId, canCpuID, 12) == 0
                            && curtain->ware_dev.devType == devType
                            && curtain->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(curtain->ware_dev.devName, devName, 12);
                                memcpy(curtain->ware_dev.roomName, roomName, 12);
                                curtain->curtain.powChn = powChn;
                            }

                            u8 data_buf[WARE_CUR_SIZE];
                            memcpy(data_buf, &curtain->ware_dev, WARE_DEV_SIZE);
                            memcpy(data_buf + WARE_DEV_SIZE, &curtain->curtain, CURTAIN_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_CUR_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_CUR_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                        }
                    }
                }
                    break;
                case e_ware_tv: {
                    node_tv *tv = tv_list.head;
                    for (int j = 0; j < curtain_list.size; j++, tv = tv->next) {
                        if (memcmp(tv->ware_dev.canCpuId, canCpuID, 12) == 0
                            && tv->ware_dev.devType == devType
                            && tv->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(tv->ware_dev.devName, devName, 12);
                                memcpy(tv->ware_dev.roomName, roomName, 12);
                            }

                            u8 data_buf[WARE_TV_SIZE];
                            memcpy(data_buf, &tv->ware_dev, WARE_TV_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_TV_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_TV_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                        }
                    }
                }
                    break;
                case e_ware_tvUP: {
                    node_tv *tvUP = tv_list.head;
                    for (int j = 0; j < curtain_list.size; j++, tvUP = tvUP->next) {
                        if (memcmp(tvUP->ware_dev.canCpuId, canCpuID, 12) == 0
                            && tvUP->ware_dev.devType == devType
                            && tvUP->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(tvUP->ware_dev.devName, devName, 12);
                                memcpy(tvUP->ware_dev.roomName, roomName, 12);
                            }

                            u8 data_buf[WARE_TVUP_SIZE];
                            memcpy(data_buf, &tvUP->ware_dev, WARE_TVUP_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_TVUP_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_TVUP_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                        }
                    }
                }
                    break;
                case e_ware_lock: {
                    node_lock *lock = lock_list.head;
                    for (int j = 0; j < curtain_list.size; j++, lock = lock->next) {
                        if (memcmp(lock->ware_dev.canCpuId, canCpuID, 12) == 0
                            && lock->ware_dev.devType == devType
                            && lock->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(lock->ware_dev.devName, devName, 12);
                                memcpy(lock->ware_dev.roomName, roomName, 12);
                                lock->lock.powChnOpen = powChn;
                            }

                            u8 data_buf[WARE_LOCK_SIZE];
                            memcpy(data_buf, &lock->ware_dev, WARE_LOCK_SIZE);
                            memcpy(data_buf + WARE_DEV_SIZE, &lock->lock, LOCK_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_LOCK_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_LOCK_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                        }
                    }
                }
                    break;
                case e_ware_fresh_air: {
                    node_frair *frair = frair_list.head;
                    for (int j = 0; j < curtain_list.size; j++, frair = frair->next) {
                        if (memcmp(frair->ware_dev.canCpuId, canCpuID, 12) == 0
                            && frair->ware_dev.devType == devType
                            && frair->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(frair->ware_dev.devName, devName, 12);
                                memcpy(frair->ware_dev.roomName, roomName, 12);
                                frair->frair.powChn = powChn;
                            }

                            u8 data_buf[WARE_FRAIR_SIZE];
                            memcpy(data_buf, &frair->ware_dev, WARE_FRAIR_SIZE);
                            memcpy(data_buf + WARE_DEV_SIZE, &frair->frair, FRAIR_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_FRAIR_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_FRAIR_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                        }
                    }
                }
                    break;
                case e_ware_value: {
                    node_valve *valve = valve_list.head;
                    for (int j = 0; j < curtain_list.size; j++, valve = valve->next) {
                        if (memcmp(valve->ware_dev.canCpuId, canCpuID, 12) == 0
                            && valve->ware_dev.devType == devType
                            && valve->ware_dev.devId == devID) {
                            if(datType == e_udpPro_editDev){
                                memcpy(valve->ware_dev.devName, devName, 12);
                                memcpy(valve->ware_dev.roomName, roomName, 12);
                                valve->valve.powChnOpen = powChn;
                            }

                            u8 data_buf[WARE_VALUE_SIZE];
                            memcpy(data_buf, &valve->ware_dev, WARE_VALUE_SIZE);
                            memcpy(data_buf + WARE_DEV_SIZE, &valve->valve, VALUE_SIZE);

                            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip),
                                                                   data_buf, WARE_VALUE_SIZE, \
                                                               datType, gw->gw_id, gw->gw_pass,
                                                                   IS_ACK, 0, value);
                            int len = UDP_PKT_SIZE + WARE_VALUE_SIZE;
                            sendto(primary_udp, send_pkt, len, 0,
                                   (struct sockaddr *) &gw->gw_sender, sizeof(gw->gw_sender));

                        }
                    }
                }
                    break;
                default:
                    break;
            }
        }
    }
}

void ctrl_all_devs_json(u8 *devUnitID, int datType, int devType, int cmd)
{

    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);

            u8 data_buf[1] = {0};
            data_buf[0] = cmd;
            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                   1, datType, gw->gw_id, gw->gw_pass,
                                                   IS_ACK, 0, devType);
            int len = UDP_PKT_SIZE + 1;
            sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                   sizeof(gw->gw_sender));

        }
    }
}

char *create_ctl_reply_info_json(UDPPROPKT *pkt)
{
    cJSON *root, *dev_rows, *item;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    u8 devUnitID[25] = {0};
    bytes_to_string(pkt->uidSrc, devUnitID, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) devUnitID));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(pkt->datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(pkt->subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(pkt->subType2));

    cJSON_AddItemToObject(root, "dev_rows", dev_rows = cJSON_CreateArray());
    cJSON_AddItemToArray(dev_rows, item = cJSON_CreateObject());

    WARE_DEV *ware_dev = (WARE_DEV *) pkt->dat;

    u8 canCpuID[25] = {0};
    bytes_to_string(ware_dev->canCpuId, canCpuID, 12);
    cJSON_AddItemToObject(item, "canCpuID", cJSON_CreateString((char *) canCpuID));

    char devName[25] = {0};
    sprintf(devName, "%x%x%x%x%x%x%x%x%x%x%x%x", ware_dev->devName[0], ware_dev->devName[1],
            ware_dev->devName[2], ware_dev->devName[3], ware_dev->devName[4], ware_dev->devName[5],
            ware_dev->devName[6], ware_dev->devName[7], ware_dev->devName[8], ware_dev->devName[9],
            ware_dev->devName[10], ware_dev->devName[11]);
    cJSON_AddItemToObject(item, "devName", cJSON_CreateString(devName));
    char roomName[25] = {0};
    sprintf(roomName, "%x%x%x%x%x%x%x%x%x%x%x%x", ware_dev->roomName[0], ware_dev->roomName[1],
            ware_dev->roomName[2], ware_dev->roomName[3], ware_dev->roomName[4],
            ware_dev->roomName[5], ware_dev->roomName[6], ware_dev->roomName[7],
            ware_dev->roomName[8], ware_dev->roomName[9], ware_dev->roomName[10],
            ware_dev->roomName[11]);
    cJSON_AddItemToObject(item, "roomName", cJSON_CreateString(roomName));

    cJSON_AddItemToObject(item, "devType", cJSON_CreateNumber(ware_dev->devType));
    cJSON_AddItemToObject(item, "devID", cJSON_CreateNumber(ware_dev->devId));

    switch (ware_dev->devType) {
        case e_ware_airCond: {
            DEV_PRO_AIRCOND *air = (DEV_PRO_AIRCOND *) ware_dev->dat;
            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(air->bOnOff));
            cJSON_AddItemToObject(item, "selMode", cJSON_CreateNumber(air->selMode));
            cJSON_AddItemToObject(item, "selTemp", cJSON_CreateNumber(air->selTemp));
            cJSON_AddItemToObject(item, "selSpd", cJSON_CreateNumber(air->selSpd));
            cJSON_AddItemToObject(item, "selDirect", cJSON_CreateNumber(air->selDirect));
            cJSON_AddItemToObject(item, "rev1", cJSON_CreateNumber(air->rev1));
            cJSON_AddItemToObject(item, "powChn", cJSON_CreateNumber(air->powChn));
        }
            break;
        case e_ware_light: {
            DEV_PRO_LIGHT *light = (DEV_PRO_LIGHT *) ware_dev->dat;
            cJSON_AddItemToObject(item, "bOnOff", cJSON_CreateNumber(light->bOnOff));
            cJSON_AddItemToObject(item, "bTuneEn", cJSON_CreateNumber(light->bTuneEn));
            cJSON_AddItemToObject(item, "lmVal", cJSON_CreateNumber(light->lmVal));
            cJSON_AddItemToObject(item, "powChn", cJSON_CreateNumber(light->powChn));
        }
            break;
        default:
            break;
    }

    return cJSON_Print(root);
}

void add_scene_json(u8 *devUnitID, char *sceneName, int devCnt, int eventId, cJSON *item, int cmd)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);
            SCENE_EVENT scene;

            memset(scene.sceneName, 0, 12);
            string_to_bytes(sceneName, scene.sceneName, strlen(sceneName));
            scene.devCnt = devCnt;
            scene.eventId = eventId;
            scene.rev2 = 0;
            scene.rev3 = 0;

            int j = 0;
            //int arrySize = cJSON_GetArraySize(item);//数组大小
            if (devCnt > 0) {
                cJSON *tasklist = item->child;//子对象
                while (tasklist != NULL) {
                    char *uid = cJSON_GetObjectItem(tasklist, "uid")->valuestring;
                    string_to_bytes(uid, scene.itemAry[j].uid, 25);
                    int devType = cJSON_GetObjectItem(tasklist, "devType")->valueint;
                    scene.itemAry[j].devType = devType;
                    int devID = cJSON_GetObjectItem(tasklist, "devID")->valueint;
                    scene.itemAry[j].devID = devID;
                    int lmVal = cJSON_GetObjectItem(tasklist, "lmVal")->valueint;
                    scene.itemAry[j].lmVal = lmVal;
                    scene.itemAry[j].rev2 = 0;
                    scene.itemAry[j].rev3 = 0;
                    int bOnOff = cJSON_GetObjectItem(tasklist, "bOnOff")->valueint;
                    scene.itemAry[j].bOnoff = bOnOff;
                    scene.itemAry[j].param1 = 0;
                    scene.itemAry[j].param2 = 0;

                    tasklist = tasklist->next;
                    j++;
                }
            }
            int data_len = 16 + 20 * scene.devCnt;
            u8 data_buf[SCENE_SIZE];
            memset(data_buf, 0, SCENE_SIZE);
            memcpy(data_buf, &scene, data_len);

            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                   SCENE_SIZE, \
                                                   cmd, gw->gw_id, gw->gw_pass,
                                                   IS_ACK, 0, 0);
            int len = UDP_PKT_SIZE + SCENE_SIZE;
            sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                   sizeof(gw->gw_sender));
        }
    }
}

void ctrl_scene_json(u8 *devUnitID, int eventId, int cmd)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);
            //devUnitID, eventId
            node_scene *scene = scene_list.head;
            for (int j = 0; j < scene_list.size; j++, scene = scene->next) {
                if (scene->scene.eventId == eventId) {
                    int data_len = 16 + 20 * scene->scene.devCnt;
                    u8 data_buf[SCENE_SIZE];
                    memcpy(data_buf, &scene->scene, data_len);

                    UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                           SCENE_SIZE, \
                                                           cmd, gw->gw_id, gw->gw_pass, IS_ACK, 0,
                                                           0);
                    int len = UDP_PKT_SIZE + SCENE_SIZE;
                    sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                           sizeof(gw->gw_sender));

                }
            }
        }
    }
}

void get_keyOpitem_json(u8 *devUnitID, int key_index, u8 *uid)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);

            u8 data_buf[12];
            memcpy(data_buf, uid, 12);

            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                   12, e_udpPro_getKeyOpItems, gw->gw_id,
                                                   gw->gw_pass, IS_ACK, 0,
                                                   key_index);
            int len = UDP_PKT_SIZE + 12;
            sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                   sizeof(gw->gw_sender));
        }
    }
}

void get_chnOpitem_json(u8 *devUnitID, int devType, int devID, u8 *uid)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);

            u8 data_buf[14];
            memcpy(data_buf, uid, 12);
            data_buf[12] = (u8) devType;
            data_buf[13] = (u8) devID;

            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                   14, e_udpPro_getChnOpItems, gw->gw_id,
                                                   gw->gw_pass, IS_ACK, 0,
                                                   0);
            int len = UDP_PKT_SIZE + 14;
            sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                   sizeof(gw->gw_sender));
        }
    }
}

char *create_get_chn_opitem_reply_json(u8 *devUnitID, u8 *cpuCanID, int devType, int devID)
{
    cJSON *root, *chn_opitem_rows, *chn_item, *chn_item_up, *chn_item_down;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    int item_num = 0;
    node_chnop_item *item = chnop_item_list.head;

    cJSON_AddItemToObject(root, "chn_opitem_rows",
                          chn_opitem_rows = cJSON_CreateArray());

    for (; item; item = item->next) {
        if (memcmp(item->devUnitID, devUnitID, 12) == 0
            && memcmp(item->chn_board_id, cpuCanID, 12) == 0
            && item->devType == devType
            && item->devID == devID) {
            item_num++;
            cJSON_AddItemToArray(chn_opitem_rows, chn_item = cJSON_CreateObject());

            u8 key_cpuCanID[25] = {0};
            bytes_to_string(item->chnop_item.devUnitID, key_cpuCanID, 12);
            cJSON_AddItemToObject(chn_item, "key_cpuCanID",
                                  cJSON_CreateString((char *) key_cpuCanID));
            cJSON_AddItemToObject(chn_item, "keyDownValid",
                                  cJSON_CreateNumber(item->chnop_item.keyDownValid));
            cJSON_AddItemToObject(chn_item, "keyUpValid",
                                  cJSON_CreateNumber(item->chnop_item.keyUpValid));
            cJSON_AddItemToObject(chn_item, "keyUpCmd", chn_item_up = cJSON_CreateArray());
            cJSON_AddItemToObject(chn_item, "keyDownCmd", chn_item_down = cJSON_CreateArray());

            for (int i = 0; i < 6; ++i) {
                cJSON_AddItemToObject(chn_item_up, "keyUpCmd",
                                      cJSON_CreateNumber(item->chnop_item.keyUpCmd[i]));
                cJSON_AddItemToObject(chn_item_down, "keyDownCmd",
                                      cJSON_CreateNumber(item->chnop_item.keyDownCmd[i]));
            }
        }
    }

    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(e_udpPro_getChnOpItems));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(item_num));
    cJSON_AddItemToObject(root, "chn_opitem", cJSON_CreateNumber(item_num));

    return cJSON_Print(root);
}

char *create_set_chn_opitem_reply_json(UDPPROPKT *pkt)
{
    cJSON *root;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    u8 devUnitID[25] = {0};
    bytes_to_string(pkt->uidSrc, devUnitID, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) devUnitID));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(pkt->datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(pkt->subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(pkt->subType2));

    u8 canCpuID[25] = {0};
    bytes_to_string(pkt->dat, canCpuID, 12);
    cJSON_AddItemToObject(root, "canCpuID", cJSON_CreateString((char *) canCpuID));
    cJSON_AddItemToObject(root, "result", cJSON_CreateNumber(pkt->dat[12]));

    return cJSON_Print(root);
}

void set_chn_opitem_json(u8 *devUnitID, u8 *canCpuID, int devType, int devID, int cnt,
                         cJSON *chnop_item, int cmd)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);

            int data_len = 14 + CHN_OPITEM_SIZE * cnt;
            u8 data_buf[data_len];
            memset(data_buf, 0 , data_len);

            memcpy(data_buf, canCpuID, 12);
            data_buf[12] = devType;
            data_buf[13] = devID;

            if (cnt > 0) {
                int j = 0;
                cJSON *tasklist = chnop_item->child;//子对象
                while (tasklist != NULL) {
                    node_chnop_item item;

                    char *uid = cJSON_GetObjectItem(tasklist, "key_cpuCanID")->valuestring;
                    string_to_bytes(uid, item.chnop_item.devUnitID, 25);

                    int keyDownValid = cJSON_GetObjectItem(tasklist, "keyDownValid")->valueint;
                    item.chnop_item.keyDownValid = keyDownValid;
                    int keyUpValid = cJSON_GetObjectItem(tasklist, "keyUpValid")->valueint;
                    item.chnop_item.keyUpValid = keyUpValid;
                    item.chnop_item.rev1 = 0;
                    item.chnop_item.rev2 = 0;
                    item.chnop_item.rev3 = 0;

                    cJSON *keyDownCmd = cJSON_GetObjectItem(tasklist, "keyDownCmd");
                    cJSON *keyUpCmd = cJSON_GetObjectItem(tasklist, "keyUpCmd");
                    for (int k = 0; k < 6; k++) {
                        item.chnop_item.keyDownCmd[k] = cJSON_GetArrayItem(keyDownCmd, k)->valueint;
                        item.chnop_item.keyUpCmd[k] = cJSON_GetArrayItem(keyUpCmd, k)->valueint;
                    }

                    memcpy(data_buf + 14 + j * CHN_OPITEM_SIZE, &item.chnop_item, CHN_OPITEM_SIZE);
                    j++;
                    tasklist = tasklist->next;
                }

                UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                       data_len, \
                                                       cmd, gw->gw_id, gw->gw_pass, IS_ACK, 0, cnt);
                int len = UDP_PKT_SIZE + data_len;
                sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                       sizeof(gw->gw_sender));

            }
        }
    }
}

char *create_get_key_opitem_reply_json(u8 *devUnitID, u8 *cpuCanID, int key_index)
{
    cJSON *root, *key_opitem_rows, *key_item;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }


    u8 uid[25] = {0};
    bytes_to_string(devUnitID, uid, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) uid));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(e_udpPro_getKeyOpItems));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(key_index));
    cJSON_AddItemToObject(root, "key_opitem_rows", key_opitem_rows = cJSON_CreateArray());

    int item_num = 0;

    node_keyop_item *item = keyop_item_list.head;
    for (int i = 0; i < keyop_item_list.size; i++, item = item->next) {
        if (memcmp(item->devUnitID, devUnitID, 12) == 0
            && memcmp(item->keyinput_board_id, cpuCanID, 12) == 0
            && item->key_index == key_index) {
            item_num++;

            cJSON_AddItemToArray(key_opitem_rows, key_item = cJSON_CreateObject());
            u8 key_cpuCanID[25] = {0};
            bytes_to_string(item->keyinput_board_id, key_cpuCanID, 12);

            u8 out_cpuCanID[25] = {0};
            bytes_to_string(item->keyop_item.devUnitID, out_cpuCanID, 12);

            cJSON_AddItemToObject(key_item, "key_cpuCanID",
                                  cJSON_CreateString((char *) key_cpuCanID));
            cJSON_AddItemToObject(key_item, "out_cpuCanID",
                                  cJSON_CreateString((char *) out_cpuCanID));
            cJSON_AddItemToObject(key_item, "devType",
                                  cJSON_CreateNumber(item->keyop_item.devType));
            cJSON_AddItemToObject(key_item, "devID",
                                  cJSON_CreateNumber(item->keyop_item.devId));
            cJSON_AddItemToObject(key_item, "keyOpCmd",
                                  cJSON_CreateNumber(item->keyop_item.keyOpCmd));
            cJSON_AddItemToObject(key_item, "keyOp",
                                  cJSON_CreateNumber(item->keyop_item.keyOp));
        }
    }

    cJSON_AddItemToObject(root, "key_opitem", cJSON_CreateNumber(item_num));
    return cJSON_Print(root);
}

void set_key_opitem_json(u8 *devUnitID, u8 *canCpuID, int key_index, int cnt, cJSON *keyop_item,
                         int cmd)
{
    node_gw_client *gw = gw_client_list.head;
    for (int i = 0; i < gw_client_list.size; i++, gw = gw->next) {
        if (memcmp(gw->gw_id, devUnitID, 12) == 0) {
            char gw_ip[16] = {0};
            sprintf(gw_ip, "%d.%d.%d.%d", gw->rcu_ip[0] & 0xff, gw->rcu_ip[1] & 0xff,
                    gw->rcu_ip[2] & 0xff, gw->rcu_ip[3] & 0xff);

            int data_len = 12 + KEY_OPITEM_SIZE * cnt;
            u8 data_buf[data_len];
            memcpy(data_buf, canCpuID, 12);

            cJSON *tasklist = keyop_item->child;//子对象
            int j = 0;
            while (tasklist != NULL) {
                KEYOP_ITEM item;
                char *uid = cJSON_GetObjectItem(tasklist, "out_cpuCanID")->valuestring;
                string_to_bytes(uid, item.devUnitID, 25);

                int devType = cJSON_GetObjectItem(tasklist, "devType")->valueint;
                item.devType = devType;

                int devID = cJSON_GetObjectItem(tasklist, "devID")->valueint;
                item.devId = devID;

                int keyOpCmd = cJSON_GetObjectItem(tasklist, "keyOpCmd")->valueint;
                item.keyOpCmd = keyOpCmd;

                int keyOp = cJSON_GetObjectItem(tasklist, "keyOp")->valueint;
                item.keyOp = keyOp;

                memcpy(data_buf + 12 + j * KEY_OPITEM_SIZE, &item, KEY_OPITEM_SIZE);
                j++;
                tasklist = tasklist->next;
            }


            UDPPROPKT *send_pkt = pre_send_udp_pkt(inet_addr((char *) gw_ip), data_buf,
                                                   data_len, \
                                                   cmd, gw->gw_id, gw->gw_pass, IS_ACK, 0,
                                                   key_index);
            int len = UDP_PKT_SIZE + data_len;
            sendto(primary_udp, send_pkt, len, 0, (struct sockaddr *) &gw->gw_sender,
                   sizeof(gw->gw_sender));

        }
    }
}

char *create_set_key_opitem_reply_json(UDPPROPKT *pkt)
{
    cJSON *root;
    root = cJSON_CreateObject();

    if (root == NULL) {
        return NULL;
    }

    u8 devUnitID[25] = {0};
    bytes_to_string(pkt->uidSrc, devUnitID, 12);
    cJSON_AddItemToObject(root, "devUnitID", cJSON_CreateString((char *) devUnitID));
    cJSON_AddItemToObject(root, "datType", cJSON_CreateNumber(pkt->datType));
    cJSON_AddItemToObject(root, "subType1", cJSON_CreateNumber(pkt->subType1));
    cJSON_AddItemToObject(root, "subType2", cJSON_CreateNumber(pkt->subType2));

    u8 canCpuID[25] = {0};
    bytes_to_string(pkt->dat, canCpuID, 12);
    cJSON_AddItemToObject(root, "canCpuID", cJSON_CreateString((char *) canCpuID));
    cJSON_AddItemToObject(root, "result", cJSON_CreateNumber(pkt->dat[12]));

    return cJSON_Print(root);
}