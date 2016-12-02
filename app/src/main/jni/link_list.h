#ifndef LINK_LIST_H
#define LINK_LIST_H

#include "udppropkt.h"

#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>

/**
 * The Node struct,
 * contains item and the pointers that point to previous node/next node.
 */

typedef struct nodercu {
    RCU_INFO item;
    // next node
    struct nodercu* next;
} node_rcu;
/**
 * The rcu_linked_list struct, contains the pointers that
 * point to first node and last node, the size of the rcu_linked_list,
 * and the function pointers.
 */
typedef struct rcu_linked_list {
    node_rcu* head;
    node_rcu* tail;
    // size of this rcu_linked_list
    int size;

    // rcu_add item to any position
    void (*rcu_add) (struct rcu_linked_list*, RCU_INFO rcu_info, int);
    // remove item from any position
    void (*rcu_remove) (struct rcu_linked_list*, u8 *devUnitID);
    // create a node with item
    node_rcu* (*rcu_create_node) (RCU_INFO rcu_info);
} rcu_linked_list;

/** create a rcu_linked_list
 */
extern rcu_linked_list rcu_create_linked_list();

typedef struct Node {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    // next node
    struct Node* next;
} Node;
/**
 * The LinkedList struct, contains the pointers that
 * point to first node and last node, the size of the LinkedList,
 * and the function pointers.
 */
typedef struct ware_linked_list {
    Node* head;
    Node* tail;

    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_add) (struct ware_linked_list*, WARE_DEV, u8 *, int);

    // remove item from any position
    void (*ware_remove) (struct ware_linked_list*, WARE_DEV, u8 * );

    //clean all item from list
    void (*ware_clean)(struct ware_linked_list*);

    // create a node with item
    Node* (*ware_create_node) (WARE_DEV ware_dev, u8 *);
} ware_linked_list;

/** create a LinkedList
 */
extern ware_linked_list ware_create_linked_list ();


typedef struct NodeAircond {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    DEV_PRO_AIRCOND aircond;
    // next node
    struct NodeAircond* next;
} Node_aircond;


typedef struct aircond_linked_list {
    Node_aircond* head;
    Node_aircond* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_aircond_add) (struct aircond_linked_list*, WARE_DEV ware_dev, DEV_PRO_AIRCOND aircond, u8 *devUnitID, int position);

    // remove item from any position
    void (*ware_aircond_remove) (struct aircond_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    //clean all item from list
    void (*ware_aircond_clean)(struct aircond_linked_list*);

    // create a node with item
    Node_aircond* (*ware_aircond_createNode) (WARE_DEV ware_dev, DEV_PRO_AIRCOND aircond, u8 *devUnitID);

} aircond_linked_list;

/** create a LinkedList
 */
extern aircond_linked_list ware_aircond_create_linked_list ();

typedef struct Nodelight {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    DEV_PRO_LIGHT light;
    // next node
    struct Nodelight* next;
} Node_light;


typedef struct light_linked_list {
    Node_light* head;
    Node_light* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_light_add) (struct light_linked_list*, WARE_DEV ware_dev, DEV_PRO_LIGHT light, u8 *devUnitID, int);

    // remove item from any position
    void (*ware_light_remove) (struct light_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    //clean all item from list
    void (*ware_light_clean)(struct light_linked_list*);

    // create a node with item
    Node_light* (*ware_light_createNode) (WARE_DEV ware_dev, DEV_PRO_LIGHT light, u8 *devUnitID);
} light_linked_list;

extern light_linked_list ware_light_create_linked_list();


typedef struct node_curtain {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    DEV_PRO_CURTAIN curtain;
    // next node
    struct node_curtain* next;
} node_curtain;

typedef struct curtain_linked_list {
    node_curtain* head;
    node_curtain* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_curtain_add) (struct curtain_linked_list*, WARE_DEV ware_dev, DEV_PRO_CURTAIN curtain, u8 *devUnitID, int);

    // remove item from any position
    void (*ware_curtain_remove) (struct curtain_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    //clean all item from list
    void (*ware_curtain_clean)(struct curtain_linked_list*);

    // create a node with item
    node_curtain* (*ware_curtain_createNode) (WARE_DEV ware_dev, DEV_PRO_CURTAIN curtain, u8 *devUnitID);
} curtain_linked_list;

/** create a LinkedList
 */
extern curtain_linked_list ware_curtain_create_linked_list ();

typedef struct node_lock {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    DEV_PRO_LOCK lock;
    // next node
    struct node_lock* next;
} node_lock;

typedef struct lock_linked_list {
    node_lock* head;
    node_lock* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_lock_add) (struct lock_linked_list*, WARE_DEV ware_dev, DEV_PRO_LOCK lock, u8 *devUnitID, int);

    // remove item from any position
    void (*ware_lock_remove) (struct lock_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    //clean all item from list
    void (*ware_lock_clean)(struct lock_linked_list*);

    // create a node with item
    node_lock* (*ware_lock_createNode) (WARE_DEV ware_dev, DEV_PRO_LOCK lock, u8 *devUnitID);
} lock_linked_list;

/** create a LinkedList
 */
extern lock_linked_list ware_lock_create_linked_list ();

typedef struct node_valve {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    DEV_PRO_VALVE valve;
    // next node
    struct node_valve* next;
} node_valve;

typedef struct valve_linked_list {
    node_valve* head;
    node_valve* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_valve_add) (struct valve_linked_list*, WARE_DEV ware_dev, DEV_PRO_VALVE valve, u8 *devUnitID, int);

    // remove item from any position
    void (*ware_valve_remove) (struct valve_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    //clean all item from list
    void (*ware_valve_clean)(struct valve_linked_list*);


    // create a node with item
    node_valve* (*ware_valve_createNode) (WARE_DEV ware_dev, DEV_PRO_VALVE valve, u8 *devUnitID);
} valve_linked_list;

/** create a LinkedList
 */
extern valve_linked_list ware_valve_create_linked_list ();

typedef struct node_frair {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    DEV_PRO_FRESHAIR frair;
    // next node
    struct node_frair* next;
} node_frair;

typedef struct frair_linked_list {
    node_frair* head;
    node_frair* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_frair_add) (struct frair_linked_list*, WARE_DEV ware_dev, DEV_PRO_FRESHAIR frair, u8 *devUnitID, int);

    // remove item from any position
    void (*ware_frair_remove) (struct frair_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    //clean all item from list
    void (*ware_frair_clean)(struct frair_linked_list*);

    // create a node with item
    node_frair* (*ware_frair_createNode) (WARE_DEV ware_dev, DEV_PRO_FRESHAIR frair, u8 *devUnitID);
} frair_linked_list;

/** create a LinkedList
 */
extern frair_linked_list ware_frair_create_linked_list ();

typedef struct node_tv {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    // next node
    struct node_tv* next;
} node_tv;

typedef struct tv_linked_list {
    node_tv* head;
    node_tv* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_tv_add) (struct tv_linked_list*, WARE_DEV ware_dev, u8 *devUnitID, int);

    // remove item from any position
    void (*ware_tv_remove) (struct tv_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    // create a node with item
    node_tv* (*ware_tv_createNode) (WARE_DEV ware_dev, u8 *devUnitID);
} tv_linked_list;

/** create a LinkedList
 */
extern tv_linked_list ware_tv_create_linked_list ();

typedef struct node_tvUP {
    u8 devUnitID[12];
    WARE_DEV ware_dev;
    // next node
    struct node_tvUP* next;
} node_tvUP;

typedef struct tvUP_linked_list {
    node_tvUP* head;
    node_tvUP* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_tvUP_add) (struct tvUP_linked_list*, WARE_DEV ware_dev, u8 *devUnitID, int);

    // remove item from any position
    void (*ware_tvUP_remove) (struct tvUP_linked_list*, WARE_DEV ware_dev, u8 *devUnitID);

    // create a node with item
    node_tvUP* (*ware_tvUP_createNode) (WARE_DEV ware_dev, u8 *devUnitID);
} tvUP_linked_list;

/** create a LinkedList
 */
extern tvUP_linked_list ware_tvUP_create_linked_list ();

typedef struct NodeScene {
    u8 devUnitID[12];
    SCENE_EVENT scene;
    // next node
    struct NodeScene* next;
} node_scene;

typedef struct scene_linked_list {
    node_scene* head;
    node_scene* tail;
    // size of this LinkedList
    int size;

    // ware_add item to any position
    void (*ware_scene_add) (struct scene_linked_list*,  SCENE_EVENT scene, u8 *devUnitID, int);
    // remove item from any position
    void (*ware_scene_remove) (struct scene_linked_list*,  SCENE_EVENT scene, u8 *devUnitID);

    // create a node with item
    node_scene* (*ware_scene_createNode) ( SCENE_EVENT scene, u8 *devUnitID);
} scene_linked_list;

/** create a LinkedList
 */
extern scene_linked_list ware_scene_create_linked_list ();

typedef struct node_board {
    u8 devUnitID[12];
    BOARD_CHNOUT board;
    // next node
    struct node_board* next;
} node_board;


typedef struct board_linked_list {
    node_board* head;
    node_board* tail;
    // size of this LinkedList
    int size;

    // board_add item to any position
    void (*board_add) (struct board_linked_list*,  BOARD_CHNOUT board, u8 *devUnitID, int);
    // remove item from any position
    void (*board_remove) (struct board_linked_list*,  BOARD_CHNOUT board, u8 *devUnitID);

    // create a node with item
    node_board* (*board_create_node) (BOARD_CHNOUT board, u8 *devUnitID);
} board_linked_list;

/** create a LinkedList
 */
extern board_linked_list board_create_linked_list ();

typedef struct node_keyinput {
    u8 devUnitID[12];
    BOARD_KEYINPUT keyinput;
    // next node
    struct node_keyinput* next;
} node_keyinput;


typedef struct keyinput_linked_list {
    node_keyinput* head;
    node_keyinput* tail;
    // size of this LinkedList
    int size;

    // keyinput_add item to any position
    void (*keyinput_add) (struct keyinput_linked_list*,  BOARD_KEYINPUT keyinput, u8 *devUnitID, int);

    // remove item from any position
    void (*keyinput_remove) (struct keyinput_linked_list*,  BOARD_KEYINPUT keyinput, u8 *devUnitID);

    // create a node with item
    node_keyinput* (*keyinput_create_node) ( BOARD_KEYINPUT keyinput, u8 *devUnitID);
} keyinput_linked_list;

/** create a LinkedList
 */
extern keyinput_linked_list keyinput_create_linked_list ();

typedef struct node_chnop_item {
    u8 devUnitID[12];
    u8 chn_board_id[12];
    int devType;
    int devID;
    int item_num;
    CHNOP_ITEM chnop_item;
    // next node
    struct node_chnop_item* next;
} node_chnop_item;


typedef struct chnop_item_linked_list {
    node_chnop_item* head;
    node_chnop_item* tail;
    // size of this LinkedList
    int size;

    // chnop_item_add item to any position
    void (*chnop_item_add) (struct chnop_item_linked_list*,  CHNOP_ITEM chnop_item, u8 *devUnitID, u8 *board_id, int devType, int devID, int num, int);
    // remove item from any position
    void (*chnop_item_remove) (struct chnop_item_linked_list*,  CHNOP_ITEM chnop_item, u8 *devUnitID);
    void (*chnop_item_clean)(struct chnop_item_linked_list*);
    // create a node with item
    node_chnop_item* (*chnop_item_create_node) ( CHNOP_ITEM chnop_item, u8 *devUnitID, u8 *board_id, int devType, int devID, int num);
} chnop_item_linked_list;

/** create a LinkedList
 */
extern chnop_item_linked_list chnop_item_create_linked_list ();

typedef struct node_keyop_item {
    u8 devUnitID[12];
    u8 keyinput_board_id[12];
    int key_index;
    KEYOP_ITEM keyop_item;
    // next node
    struct node_keyop_item* next;
} node_keyop_item;


typedef struct keyop_item_linked_list {
    node_keyop_item* head;
    node_keyop_item* tail;
    // size of this LinkedList
    int size;

    // keyop_item_add item to any position
    void (*keyop_item_add) (struct keyop_item_linked_list*,  KEYOP_ITEM keyop_item, u8 *devUnitID, u8 *keyinput_board_id, int key_index, int);
    // remove item from any position
    void (*keyop_item_remove) (struct keyop_item_linked_list*, u8 *devUnitID, u8 *keyinput_board_id, int key_index);

    // create a node with item
    node_keyop_item* (*keyop_item_create_node) ( KEYOP_ITEM keyop_item, u8 *devUnitID, u8 *keyinput_board_id, int index);
} keyop_item_linked_list;

/** create a LinkedList
 */
extern keyop_item_linked_list keyop_item_create_linked_list ();


typedef struct node_gw_client
{
    struct sockaddr_in    gw_sender;
    u8                    gw_id[12];
    u8                    gw_pass[8];
    u8                    rcu_ip[4];
    // next node
    struct node_gw_client* next;
} node_gw_client;

typedef struct gw_client_linked_list {
    node_gw_client* head;
    node_gw_client* tail;
    // size of this LinkedList
    int size;

    // keyop_item_add item to any position
    void (*gw_client_add) (struct gw_client_linked_list*,  struct sockaddr_in , u8 *, u8 *, u8 *,int);

    // remove item from any position
    void (*gw_client_remove) (struct gw_client_linked_list*,u8 *, u8 *);

    // create a node with item
    node_gw_client* (*gw_client_create_node) (struct sockaddr_in , u8 *, u8 *, u8 *);
} gw_client_linked_list;

/** create a LinkedList
 */
extern gw_client_linked_list gw_client_create_linked_list ();

typedef struct node_app_client
{
    struct sockaddr_in    app_sender;
    u8                    app_ip[4];
    // next node
    struct node_app_client* next;
} node_app_client;

typedef struct app_client_linked_list {
    node_app_client* head;
    node_app_client* tail;
    // size of this LinkedList
    int size;

    // keyop_item_add item to any position
    void (*app_client_add) (struct app_client_linked_list*,  struct sockaddr_in , u8 *, int);

    // remove item from any position
    void (*app_client_remove) (struct app_client_linked_list*, struct sockaddr_in);

    // create a node with item
    node_app_client* (*app_client_create_node) (struct sockaddr_in, u8 *app_ip);
} app_client_linked_list;

/** create a LinkedList
 */
extern app_client_linked_list app_client_create_linked_list ();

typedef struct node_udp_msg_queue
{
    u8 devUnitID[12];
    int cmd;
    int id;
    int flag;
    // next node
    struct node_udp_msg_queue* next;
} node_udp_msg_queue;

typedef struct udp_msg_queue_linked_list {
    node_udp_msg_queue* head;
    node_udp_msg_queue* tail;
    // size of this LinkedList
    int size;

    // keyop_item_add item to any position
    void (*udp_msg_queue_add) (struct udp_msg_queue_linked_list*, u8 *, int ,int, int, int);

    // create a node with item
    node_udp_msg_queue* (*udp_msg_queue_create_node) (u8 *, int ,int, int);
} udp_msg_queue_linked_list;

/** create a LinkedList
 */
extern udp_msg_queue_linked_list udp_msg_queue_create_linked_list ();

#endif // LINK_LIST_H
