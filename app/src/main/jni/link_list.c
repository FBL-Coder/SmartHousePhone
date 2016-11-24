#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>

#include "link_list.h"
#include "debug.h"
#include "udppropkt.h"

/** rcu_add item to any position
 */
void rcu_add(rcu_linked_list *_this, RCU_INFO item, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // rcu_add to head
    if (position == 0) {
        _this->rcu_addFirst(_this, item);
    } else if (position == _this->size) {
        // rcu_add to tail
        _this->rcu_addLast(_this, item);
    }
}

/** rcu_add item to head
 */
void rcu_addFirst(rcu_linked_list *_this, RCU_INFO item)
{
    node_rcu *newNode = _this->rcu_create_node(item);
    node_rcu *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_rcu *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        while (head) {

            if (memcmp(head->item.devUnitID, item.devUnitID, 12) == 0) {
                head->item = item;
                return;
            }
            head = head->next;
        }
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** rcu_add item to tail
 */
void rcu_addLast(rcu_linked_list *_this, RCU_INFO item)
{
    node_rcu *newNode = _this->rcu_create_node(item);
    node_rcu *head = _this->head;
    node_rcu *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_rcu *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {

            if (memcmp(head->item.devUnitID, item.devUnitID, 12) == 0) {
                head->item = item;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** rcu_get item from specific position
 */
RCU_INFO rcu_get(rcu_linked_list *_this, u8 *devUnitID, u8 *devUnitPass)
{

    RCU_INFO rcu_info;
    memset(&rcu_info, 0, sizeof(RCU_INFO));

    node_rcu *node = _this->head;
    int i = 0;
    // loop until position
    while (i < _this->size && memcmp(node->item.devUnitID, devUnitID, 12) != 0) {
        node = node->next;
        i++;
    }
    if (node != NULL) {
        memcpy(node->item.devUnitPass, devUnitPass, 8);
        return node->item;
    } else
        return rcu_info;
}

/** rcu_get item and rcu_remove it
 */
void rcu_remove(rcu_linked_list *_this, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }

    node_rcu *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->item.devUnitID, devUnitID, 12) == 0) {
            // rcu_remove node from list
            node_rcu *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }

}


/** rcu_display the items in the list
 */
void rcu_display(rcu_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        u8 str[25] = {0};
        node_rcu *node = _this->head;
        for (i = 0; i < size; i++) {

            bytes_to_string(node->item.devUnitID, str, 12);
            node = node->next;
        }
    }
}

/** create a node_rcu
 */
node_rcu *rcu_create_node(RCU_INFO item)
{
    node_rcu *node = (node_rcu *) malloc(sizeof(node_rcu));
    node->item = item;
    node->prev = NULL;
    node->next = NULL;
    return node;
}

/** create a rcu_linked_list
 */
rcu_linked_list rcu_create_linked_list()
{
    rcu_linked_list list;

    list.head = NULL;
    list.tail = NULL;
    list.rcu_add = &rcu_add;
    list.rcu_addFirst = &rcu_addFirst;
    list.rcu_addLast = &rcu_addLast;
    list.rcu_get = &rcu_get;
    list.rcu_remove = &rcu_remove;
    list.rcu_display = &rcu_display;
    list.rcu_create_node = &rcu_create_node;
    list.size = 0;

    return list;
}

/** ware_add item to any position
 */
void ware_add(ware_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_addFirst(_this, ware_dev, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_addLast(_this, ware_dev, devUnitID);
    }
}

/** ware_add item to head
 */
void ware_addFirst(ware_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    Node *newNode = _this->ware_create_node(ware_dev, devUnitID);
    Node *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        Node *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_addLast(ware_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    Node *newNode = _this->ware_create_node(ware_dev, devUnitID);
    Node *head = _this->head;
    Node *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        Node *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {

            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** ware_get item and remove it from any position
 */
void ware_remove(ware_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    Node *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) != 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) != 0
            && node->ware_dev.devId != ware_dev.devId
            && node->ware_dev.devType != ware_dev.devType) {

            // remove node from list
            Node *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }
}

/** ware_display the items in the list
 */
void ware_display(ware_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        Node *node = _this->head;
        for (i = 0; i < size; i++) {

            node = node->next;
        }
    }
}

/** create a Node
 */
Node *ware_create_node(WARE_DEV item, u8 *devUnitID)
{
    Node *node = (Node *) malloc(sizeof(Node));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = item;
    node->prev = NULL;
    node->next = NULL;
    return node;
}

/** create a ware_linked_list
 */
ware_linked_list ware_create_linked_list()
{
    ware_linked_list list;

    list.head = NULL;
    list.tail = NULL;
    list.ware_add = &ware_add;
    list.ware_addFirst = &ware_addFirst;
    list.ware_addLast = &ware_addLast;
    list.ware_remove = &ware_remove;
    list.ware_display = &ware_display;
    list.ware_create_node = &ware_create_node;
    list.size = 0;

    return list;
}


/** ware_aircond_add item to any position
 */
void ware_aircond_add(aircond_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_AIRCOND aircond,
                      u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }

    // ware_add to head
    if (position == 0) {
        _this->ware_aircond_addFirst(_this, ware_dev, aircond, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_aircond_addLast(_this, ware_dev, aircond, devUnitID);
    }
}

/** ware_aircond_add item to head
 */
void ware_aircond_addFirst(aircond_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_AIRCOND aircond,
                           u8 *devUnitID)
{
    Node_aircond *newNode = _this->ware_aircond_createNode(ware_dev, aircond, devUnitID);
    Node_aircond *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        Node_aircond *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_aircond_addLast(aircond_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_AIRCOND aircond,
                          u8 *devUnitID)
{
    Node_aircond *newNode = _this->ware_aircond_createNode(ware_dev, aircond, devUnitID);
    Node_aircond *head = _this->head;
    Node_aircond *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        Node_aircond *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                head->aircond = aircond;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_aircond_remove(aircond_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    Node_aircond *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            Node_aircond *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }

}

/** display the items in the list
 */
void ware_aircond_display(aircond_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        Node_aircond *node = _this->head;
        for (i = 0; i < size; i++) {
            node = node->next;
        }
    }
}

/** create a Node
 */
Node_aircond *ware_aircond_createNode(WARE_DEV ware_dev, DEV_PRO_AIRCOND ware_aircond,
                                      u8 *devUnitID)
{
    Node_aircond *node = (Node_aircond *) malloc(sizeof(Node_aircond));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->aircond = ware_aircond;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
aircond_linked_list ware_aircond_create_linked_list()
{
    aircond_linked_list ware_aircond_list;

    ware_aircond_list.head = NULL;
    ware_aircond_list.tail = NULL;
    ware_aircond_list.ware_aircond_add = &ware_aircond_add;
    ware_aircond_list.ware_aircond_addFirst = &ware_aircond_addFirst;
    ware_aircond_list.ware_aircond_addLast = &ware_aircond_addLast;
    ware_aircond_list.ware_aircond_remove = &ware_aircond_remove;
    ware_aircond_list.ware_aircond_display = &ware_aircond_display;
    ware_aircond_list.ware_aircond_createNode = &ware_aircond_createNode;

    ware_aircond_list.size = 0;

    return ware_aircond_list;
}

/** ware_ware_light_add item to any position
 */
void ware_light_add(light_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_LIGHT ware_light,
                    u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_light_addFirst(_this, ware_dev, ware_light, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_light_addLast(_this, ware_dev, ware_light, devUnitID);
    }
}

/** ware_ware_light_add item to head
 */
void ware_light_addFirst(light_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_LIGHT ware_light,
                         u8 *devUnitID)
{
    Node_light *newNode = _this->ware_light_createNode(ware_dev, ware_light, devUnitID);
    Node_light *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        Node_light *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_light_addLast(light_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_LIGHT ware_light,
                        u8 *devUnitID)
{
    Node_light *newNode = _this->ware_light_createNode(ware_dev, ware_light, devUnitID);
    Node_light *head = _this->head;
    Node_light *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        Node_light *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {

            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                head->light.bOnOff = ware_light.bOnOff;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_light_remove(light_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    Node_light *node = _this->head;

    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            Node_light *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }

}

/** display the items in the list
 */
void ware_light_display(light_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        Node_light *node = _this->head;
        for (i = 0; i < size; i++) {
            node = node->next;
        }
    }
}

/** create a Node
 */
Node_light *ware_light_createNode(WARE_DEV ware_dev, DEV_PRO_LIGHT ware_light, u8 *devUnitID)
{
    Node_light *node = (Node_light *) malloc(sizeof(Node_light));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->light = ware_light;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
light_linked_list ware_light_create_linked_list()
{
    light_linked_list ware_light_list;

    ware_light_list.head = NULL;
    ware_light_list.tail = NULL;
    ware_light_list.ware_light_add = &ware_light_add;
    ware_light_list.ware_light_addFirst = &ware_light_addFirst;
    ware_light_list.ware_light_addLast = &ware_light_addLast;
    ware_light_list.ware_light_remove = &ware_light_remove;
    ware_light_list.ware_light_display = &ware_light_display;
    ware_light_list.ware_light_createNode = &ware_light_createNode;
    ware_light_list.size = 0;

    return ware_light_list;
}


/** ware_ware_curtain_add item to any position
 */
void ware_curtain_add(curtain_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_CURTAIN ware_curtain,
                      u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_curtain_addFirst(_this, ware_dev, ware_curtain, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_curtain_addLast(_this, ware_dev, ware_curtain, devUnitID);
    }
}

/** ware_ware_curtain_add item to head
 */
void ware_curtain_addFirst(curtain_linked_list *_this, WARE_DEV ware_dev,
                           DEV_PRO_CURTAIN ware_curtain, u8 *devUnitID)
{
    node_curtain *newNode = _this->ware_curtain_createNode(ware_dev, ware_curtain, devUnitID);
    node_curtain *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_curtain *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_curtain_addLast(curtain_linked_list *_this, WARE_DEV ware_dev,
                          DEV_PRO_CURTAIN ware_curtain, u8 *devUnitID)
{
    node_curtain *newNode = _this->ware_curtain_createNode(ware_dev, ware_curtain, devUnitID);
    node_curtain *head = _this->head;
    node_curtain *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_curtain *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                head->curtain = ware_curtain;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_curtain_remove(curtain_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_curtain *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            node_curtain *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }


}

/** display the items in the list
 */
void ware_curtain_display(curtain_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_curtain *node = _this->head;
        for (i = 0; i < size; i++) {

            node = node->next;
        }
    }
}

/** create a Node
 */
node_curtain *ware_curtain_createNode(WARE_DEV ware_dev, DEV_PRO_CURTAIN ware_curtain,
                                      u8 *devUnitID)
{
    node_curtain *node = (node_curtain *) malloc(sizeof(node_curtain));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->curtain = ware_curtain;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
curtain_linked_list ware_curtain_create_linked_list()
{
    curtain_linked_list ware_curtain_list;

    ware_curtain_list.head = NULL;
    ware_curtain_list.tail = NULL;
    ware_curtain_list.ware_curtain_add = &ware_curtain_add;
    ware_curtain_list.ware_curtain_addFirst = &ware_curtain_addFirst;
    ware_curtain_list.ware_curtain_addLast = &ware_curtain_addLast;
    ware_curtain_list.ware_curtain_remove = &ware_curtain_remove;
    ware_curtain_list.ware_curtain_display = &ware_curtain_display;
    ware_curtain_list.ware_curtain_createNode = &ware_curtain_createNode;
    ware_curtain_list.size = 0;

    return ware_curtain_list;
}


/** ware_ware_lock_add item to any position
 */
void ware_lock_add(lock_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_LOCK ware_lock,
                   u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_lock_addFirst(_this, ware_dev, ware_lock, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_lock_addLast(_this, ware_dev, ware_lock, devUnitID);
    }
}

/** ware_ware_lock_add item to head
 */
void ware_lock_addFirst(lock_linked_list *_this, WARE_DEV ware_dev,
                        DEV_PRO_LOCK ware_lock, u8 *devUnitID)
{
    node_lock *newNode = _this->ware_lock_createNode(ware_dev, ware_lock, devUnitID);
    node_lock *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_lock *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_lock_addLast(lock_linked_list *_this, WARE_DEV ware_dev,
                       DEV_PRO_LOCK ware_lock, u8 *devUnitID)
{
    node_lock *newNode = _this->ware_lock_createNode(ware_dev, ware_lock, devUnitID);
    node_lock *head = _this->head;
    node_lock *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_lock *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                head->lock = ware_lock;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_lock_remove(lock_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_lock *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            node_lock *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }


}

/** display the items in the list
 */
void ware_lock_display(lock_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_lock *node = _this->head;
        for (i = 0; i < size; i++) {

            node = node->next;
        }
    }
}

/** create a Node
 */
node_lock *ware_lock_createNode(WARE_DEV ware_dev, DEV_PRO_LOCK ware_lock,
                                u8 *devUnitID)
{
    node_lock *node = (node_lock *) malloc(sizeof(node_lock));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->lock = ware_lock;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
lock_linked_list ware_lock_create_linked_list()
{
    lock_linked_list ware_lock_list;

    ware_lock_list.head = NULL;
    ware_lock_list.tail = NULL;
    ware_lock_list.ware_lock_add = &ware_lock_add;
    ware_lock_list.ware_lock_addFirst = &ware_lock_addFirst;
    ware_lock_list.ware_lock_addLast = &ware_lock_addLast;
    ware_lock_list.ware_lock_remove = &ware_lock_remove;
    ware_lock_list.ware_lock_display = &ware_lock_display;
    ware_lock_list.ware_lock_createNode = &ware_lock_createNode;
    ware_lock_list.size = 0;

    return ware_lock_list;
}


/** ware_ware_valve_add item to any position
 */
void ware_valve_add(valve_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_VALVE ware_valve,
                    u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_valve_addFirst(_this, ware_dev, ware_valve, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_valve_addLast(_this, ware_dev, ware_valve, devUnitID);
    }
}

/** ware_ware_valve_add item to head
 */
void ware_valve_addFirst(valve_linked_list *_this, WARE_DEV ware_dev,
                         DEV_PRO_VALVE ware_valve, u8 *devUnitID)
{
    node_valve *newNode = _this->ware_valve_createNode(ware_dev, ware_valve, devUnitID);
    node_valve *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_valve *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_valve_addLast(valve_linked_list *_this, WARE_DEV ware_dev,
                        DEV_PRO_VALVE ware_valve, u8 *devUnitID)
{
    node_valve *newNode = _this->ware_valve_createNode(ware_dev, ware_valve, devUnitID);
    node_valve *head = _this->head;
    node_valve *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_valve *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                head->valve = ware_valve;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_valve_remove(valve_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_valve *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            node_valve *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }


}

/** display the items in the list
 */
void ware_valve_display(valve_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_valve *node = _this->head;
        for (i = 0; i < size; i++) {

            node = node->next;
        }
    }
}

/** create a Node
 */
node_valve *ware_valve_createNode(WARE_DEV ware_dev, DEV_PRO_VALVE ware_valve,
                                  u8 *devUnitID)
{
    node_valve *node = (node_valve *) malloc(sizeof(node_valve));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->valve = ware_valve;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
valve_linked_list ware_valve_create_linked_list()
{
    valve_linked_list ware_valve_list;

    ware_valve_list.head = NULL;
    ware_valve_list.tail = NULL;
    ware_valve_list.ware_valve_add = &ware_valve_add;
    ware_valve_list.ware_valve_addFirst = &ware_valve_addFirst;
    ware_valve_list.ware_valve_addLast = &ware_valve_addLast;
    ware_valve_list.ware_valve_remove = &ware_valve_remove;
    ware_valve_list.ware_valve_display = &ware_valve_display;
    ware_valve_list.ware_valve_createNode = &ware_valve_createNode;
    ware_valve_list.size = 0;

    return ware_valve_list;
}

/** ware_ware_frair_add item to any position
 */
void ware_frair_add(frair_linked_list *_this, WARE_DEV ware_dev, DEV_PRO_FRESHAIR ware_frair,
                    u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_frair_addFirst(_this, ware_dev, ware_frair, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_frair_addLast(_this, ware_dev, ware_frair, devUnitID);
    }
}

/** ware_ware_frair_add item to head
 */
void ware_frair_addFirst(frair_linked_list *_this, WARE_DEV ware_dev,
                         DEV_PRO_FRESHAIR ware_frair, u8 *devUnitID)
{
    node_frair *newNode = _this->ware_frair_createNode(ware_dev, ware_frair, devUnitID);
    node_frair *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_frair *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_frair_addLast(frair_linked_list *_this, WARE_DEV ware_dev,
                        DEV_PRO_FRESHAIR ware_frair, u8 *devUnitID)
{
    node_frair *newNode = _this->ware_frair_createNode(ware_dev, ware_frair, devUnitID);
    node_frair *head = _this->head;
    node_frair *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_frair *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                head->frair = ware_frair;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_frair_remove(frair_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_frair *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            node_frair *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }


}

/** display the items in the list
 */
void ware_frair_display(frair_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_frair *node = _this->head;
        for (i = 0; i < size; i++) {

            node = node->next;
        }
    }
}

/** create a Node
 */
node_frair *ware_frair_createNode(WARE_DEV ware_dev, DEV_PRO_FRESHAIR ware_frair,
                                  u8 *devUnitID)
{
    node_frair *node = (node_frair *) malloc(sizeof(node_frair));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->frair = ware_frair;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
frair_linked_list ware_frair_create_linked_list()
{
    frair_linked_list ware_frair_list;

    ware_frair_list.head = NULL;
    ware_frair_list.tail = NULL;
    ware_frair_list.ware_frair_add = &ware_frair_add;
    ware_frair_list.ware_frair_addFirst = &ware_frair_addFirst;
    ware_frair_list.ware_frair_addLast = &ware_frair_addLast;
    ware_frair_list.ware_frair_remove = &ware_frair_remove;
    ware_frair_list.ware_frair_display = &ware_frair_display;
    ware_frair_list.ware_frair_createNode = &ware_frair_createNode;
    ware_frair_list.size = 0;

    return ware_frair_list;
}

/** ware_ware_tv_add item to any position
 */
void ware_tv_add(tv_linked_list *_this, WARE_DEV ware_dev,
                 u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_tv_addFirst(_this, ware_dev, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_tv_addLast(_this, ware_dev, devUnitID);
    }
}

/** ware_ware_tv_add item to head
 */
void ware_tv_addFirst(tv_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    node_tv *newNode = _this->ware_tv_createNode(ware_dev, devUnitID);
    node_tv *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_tv *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_tv_addLast(tv_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    node_tv *newNode = _this->ware_tv_createNode(ware_dev, devUnitID);
    node_tv *head = _this->head;
    node_tv *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_tv *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_tv_remove(tv_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_tv *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            node_tv *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }
}

/** display the items in the list
 */
void ware_tv_display(tv_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_tv *node = _this->head;
        for (i = 0; i < size; i++) {

            node = node->next;
        }
    }
}

/** create a Node
 */
node_tv *ware_tv_createNode(WARE_DEV ware_dev,u8 *devUnitID)
{
    node_tv *node = (node_tv *) malloc(sizeof(node_tv));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
tv_linked_list ware_tv_create_linked_list()
{
    tv_linked_list ware_tv_list;

    ware_tv_list.head = NULL;
    ware_tv_list.tail = NULL;
    ware_tv_list.ware_tv_add = &ware_tv_add;
    ware_tv_list.ware_tv_addFirst = &ware_tv_addFirst;
    ware_tv_list.ware_tv_addLast = &ware_tv_addLast;
    ware_tv_list.ware_tv_remove = &ware_tv_remove;
    ware_tv_list.ware_tv_display = &ware_tv_display;
    ware_tv_list.ware_tv_createNode = &ware_tv_createNode;
    ware_tv_list.size = 0;

    return ware_tv_list;
}


/** ware_ware_tvUP_add item to any position
 */
void ware_tvUP_add(tvUP_linked_list *_this, WARE_DEV ware_dev,
                   u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_tvUP_addFirst(_this, ware_dev, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_tvUP_addLast(_this, ware_dev, devUnitID);
    }
}

/** ware_ware_tvUP_add item to head
 */
void ware_tvUP_addFirst(tvUP_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    node_tvUP *newNode = _this->ware_tvUP_createNode(ware_dev, devUnitID);
    node_tvUP *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_tvUP *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void ware_tvUP_addLast(tvUP_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    node_tvUP *newNode = _this->ware_tvUP_createNode(ware_dev, devUnitID);
    node_tvUP *head = _this->head;
    node_tvUP *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_tvUP *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
                && head->ware_dev.devId == ware_dev.devId
                && head->ware_dev.devType == ware_dev.devType) {
                head->ware_dev = ware_dev;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void ware_tvUP_remove(tvUP_linked_list *_this, WARE_DEV ware_dev, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_tvUP *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->ware_dev.canCpuId, ware_dev.canCpuId, 12) == 0
            && node->ware_dev.devId == ware_dev.devId
            && node->ware_dev.devType == ware_dev.devType) {

            // remove node from list
            node_tvUP *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }
}

/** display the items in the list
 */
void ware_tvUP_display(tvUP_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_tvUP *node = _this->head;
        for (i = 0; i < size; i++) {

            node = node->next;
        }
    }
}

/** create a Node
 */
node_tvUP *ware_tvUP_createNode(WARE_DEV ware_dev,u8 *devUnitID)
{
    node_tvUP *node = (node_tvUP *) malloc(sizeof(node_tvUP));
    memcpy(node->devUnitID, devUnitID, 12);
    node->ware_dev = ware_dev;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
tvUP_linked_list ware_tvUP_create_linked_list()
{
    tvUP_linked_list ware_tvUP_list;

    ware_tvUP_list.head = NULL;
    ware_tvUP_list.tail = NULL;
    ware_tvUP_list.ware_tvUP_add = &ware_tvUP_add;
    ware_tvUP_list.ware_tvUP_addFirst = &ware_tvUP_addFirst;
    ware_tvUP_list.ware_tvUP_addLast = &ware_tvUP_addLast;
    ware_tvUP_list.ware_tvUP_remove = &ware_tvUP_remove;
    ware_tvUP_list.ware_tvUP_display = &ware_tvUP_display;
    ware_tvUP_list.ware_tvUP_createNode = &ware_tvUP_createNode;
    ware_tvUP_list.size = 0;

    return ware_tvUP_list;
}

/** ware_scene_add item to any position
 */
void ware_scene_add(scene_linked_list *_this, SCENE_EVENT scene, u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->ware_scene_addFirst(_this, scene, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->ware_scene_addLast(_this, scene, devUnitID);
    }
}

/** ware_scene_add item to head
 */
void ware_scene_addFirst(scene_linked_list *_this, SCENE_EVENT scene, u8 *devUnitID)
{
    node_scene *newNode = _this->ware_scene_createNode(scene, devUnitID);
    node_scene *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_scene *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_sense item to tail
 */
void ware_scene_addLast(scene_linked_list *_this, SCENE_EVENT scene, u8 *devUnitID)
{
    node_scene *newNode = _this->ware_scene_createNode(scene, devUnitID);
    node_scene *head = _this->head;
    node_scene *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_scene *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && head->scene.eventId == scene.eventId) {
                head->scene = scene;
//                  for(int i = 0; i < scene.devCnt; i++) {
//                      head->scene.itemAry[i] = scene.itemAry[i];
//                  }
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;
    }
}

/** get item and remove it from any position
 */
void ware_scene_remove(scene_linked_list *_this, SCENE_EVENT scene_event, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_scene *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && node->scene.eventId == scene_event.eventId) {

            // remove node from list
            node_scene *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }


}

/** display the items in the list
 */
void ware_scene_display(scene_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_scene *node = _this->head;
        for (i = 0; i < size; i++) {
            node = node->next;
        }
    }
}

/** create a Node
 */
node_scene *ware_scene_createNode(SCENE_EVENT scene, u8 *devUnitID)
{
    node_scene *node = (node_scene *) malloc(sizeof(node_scene));
    memcpy(node->devUnitID, devUnitID, 12);
    node->scene = scene;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
scene_linked_list ware_scene_create_linked_list()
{
    scene_linked_list ware_scene_list;

    ware_scene_list.head = NULL;
    ware_scene_list.tail = NULL;
    ware_scene_list.ware_scene_add = &ware_scene_add;
    ware_scene_list.ware_scene_addFirst = &ware_scene_addFirst;
    ware_scene_list.ware_scene_addLast = &ware_scene_addLast;
    ware_scene_list.ware_scene_remove = &ware_scene_remove;

    ware_scene_list.ware_scene_display = &ware_scene_display;
    ware_scene_list.ware_scene_createNode = &ware_scene_createNode;
    ware_scene_list.size = 0;

    return ware_scene_list;
}


/** board_add item to any position
 */
void board_add(board_linked_list *_this, BOARD_CHNOUT board, u8 *devUnitID, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->board_addFirst(_this, board, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->board_addLast(_this, board, devUnitID);
    }
}

/** board_add item to head
 */
void board_addFirst(board_linked_list *_this, BOARD_CHNOUT board, u8 *devUnitID)
{
    node_board *newNode = _this->board_create_node(board, devUnitID);
    node_board *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_board *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void board_addLast(board_linked_list *_this, BOARD_CHNOUT board, u8 *devUnitID)
{
    node_board *newNode = _this->board_create_node(board, devUnitID);
    node_board *head = _this->head;
    node_board *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_board *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {

            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->board.devUnitID, board.devUnitID, 12) == 0) {
                head->board = board;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void board_remove(board_linked_list *_this, BOARD_CHNOUT board, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_board *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->board.devUnitID, board.devUnitID, 12) == 0) {
            node_board *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }
}

/** display the items in the list
 */
void board_display(board_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_board *node = _this->head;
        u8 str[25] = {0};

        for (i = 0; i < size; i++) {
            bytes_to_string(node->board.devUnitID, str, 12);
            node = node->next;
        }
    }
}

/** create a Node
 */
node_board *board_create_node(BOARD_CHNOUT board, u8 *devUnitID)
{
    node_board *node = (node_board *) malloc(sizeof(node_board));
    memcpy(node->devUnitID, devUnitID, 12);
    node->board = board;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
board_linked_list board_create_linked_list()
{
    board_linked_list board_list;

    board_list.head = NULL;
    board_list.tail = NULL;
    board_list.board_add = &board_add;
    board_list.board_addFirst = &board_addFirst;
    board_list.board_addLast = &board_addLast;
    board_list.board_remove = &board_remove;
    board_list.board_display = &board_display;
    board_list.board_create_node = &board_create_node;
    board_list.size = 0;

    return board_list;
}


/** keyinput_add item to any position
 */
void keyinput_add(keyinput_linked_list *_this, BOARD_KEYINPUT keyinput, u8 *devUnitID,
                  int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->keyinput_addFirst(_this, keyinput, devUnitID);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->keyinput_addLast(_this, keyinput, devUnitID);
    }
}

/** keyinput_add item to head
 */
void keyinput_addFirst(keyinput_linked_list *_this, BOARD_KEYINPUT keyinput, u8 *devUnitID)
{
    node_keyinput *newNode = _this->keyinput_create_node(keyinput, devUnitID);
    node_keyinput *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_keyinput *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void keyinput_addLast(keyinput_linked_list *_this, BOARD_KEYINPUT keyinput, u8 *devUnitID)
{
    node_keyinput *newNode = _this->keyinput_create_node(keyinput, devUnitID);
    node_keyinput *head = _this->head;
    node_keyinput *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_keyinput *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {

            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->keyinput.devUnitID, keyinput.devUnitID, 12) == 0) {
                head->keyinput = keyinput;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** get item and remove it from any position
 */
void keyinput_remove(keyinput_linked_list *_this, BOARD_KEYINPUT keyinput, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_keyinput *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->keyinput.devUnitID, keyinput.devUnitID, 12) == 0) {
            node_keyinput *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }
}

/** display the items in the list
 */
void keyinput_display(keyinput_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        u8 uid[25] = {0};
        node_keyinput *node = _this->head;
        for (i = 0; i < size; i++) {
            bytes_to_string(node->keyinput.devUnitID, uid, 12);
            node = node->next;
        }
    }
}

/** create a Node
 */
node_keyinput *keyinput_create_node(BOARD_KEYINPUT keyinput, u8 *devUnitID)
{
    node_keyinput *node = (node_keyinput *) malloc(sizeof(node_keyinput));
    memcpy(node->devUnitID, devUnitID, 12);
    node->keyinput = keyinput;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
keyinput_linked_list keyinput_create_linked_list()
{
    keyinput_linked_list keyinput_list;

    keyinput_list.head = NULL;
    keyinput_list.tail = NULL;
    keyinput_list.keyinput_add = &keyinput_add;
    keyinput_list.keyinput_addFirst = &keyinput_addFirst;
    keyinput_list.keyinput_addLast = &keyinput_addLast;
    keyinput_list.keyinput_remove = &keyinput_remove;
    keyinput_list.keyinput_display = &keyinput_display;
    keyinput_list.keyinput_create_node = &keyinput_create_node;
    keyinput_list.size = 0;

    return keyinput_list;
}


/** chnop_item_add item to any position
 */
void chnop_item_add(chnop_item_linked_list *_this, CHNOP_ITEM chnop_item, u8 *devUnitID,
                    u8 *board_id, int devType, int devID, int num, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->chnop_item_addFirst(_this, chnop_item, devUnitID, board_id, devType, devID, num);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->chnop_item_addLast(_this, chnop_item, devUnitID, board_id, devType, devID, num);
    }
}

/** chnop_item_add item to head
 */
void chnop_item_addFirst(chnop_item_linked_list *_this, CHNOP_ITEM chnop_item, u8 *devUnitID,
                         u8 *board_id, int devType, int devID, int num)
{
    node_chnop_item *newNode = _this->chnop_item_create_node(chnop_item, devUnitID, board_id,
                                                             devType, devID, num);
    node_chnop_item *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_chnop_item *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void chnop_item_addLast(chnop_item_linked_list *_this, CHNOP_ITEM chnop_item, u8 *devUnitID,
                        u8 *board_id, int devType, int devID, int num)
{
    node_chnop_item *newNode = _this->chnop_item_create_node(chnop_item, devUnitID, board_id,
                                                             devType, devID, num);
    node_chnop_item *head = _this->head;
    node_chnop_item *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_chnop_item *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {

            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->chnop_item.devUnitID, chnop_item.devUnitID, 12) == 0) {
                head->chnop_item = chnop_item;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}


/** get item and remove it from any position
 */
void chnop_item_remove(chnop_item_linked_list *_this, CHNOP_ITEM chnop_item, u8 *devUnitID)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_chnop_item *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) != 0
            || memcmp(node->chnop_item.devUnitID, chnop_item.devUnitID, 12) != 0) {
            node_chnop_item *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }
}


/** get item and remove it from any position
 */
void chnop_item_clean(chnop_item_linked_list *_this)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_chnop_item *node = _this->head;
    node_chnop_item *next;

    // loop until position
    while (node) {
        next = node->next;
        free(node);
        node = next;
    }
    _this->head = NULL;
}

/** display the items in the list
 */
void chnop_item_display(chnop_item_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        u8 uid[25] = {0};
        node_chnop_item *node = _this->head;
        for (i = 0; i < size; i++) {
            bytes_to_string(node->chnop_item.devUnitID, uid, 12);
            node = node->next;
        }
    }
}

/** create a Node
 */
node_chnop_item *chnop_item_create_node(CHNOP_ITEM chnop_item, u8 *devUnitID, u8 *board_id,
                                        int devType, int devID, int num)
{
    node_chnop_item *node = (node_chnop_item *) malloc(sizeof(node_chnop_item));
    memcpy(node->devUnitID, devUnitID, 12);
    memcpy(node->chn_board_id, board_id, 12);
    node->devType = devType;
    node->devID = devID;
    node->item_num = num;
    node->chnop_item = chnop_item;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
chnop_item_linked_list chnop_item_create_linked_list()
{
    chnop_item_linked_list chnop_item_list;

    chnop_item_list.head = NULL;
    chnop_item_list.tail = NULL;
    chnop_item_list.chnop_item_add = &chnop_item_add;
    chnop_item_list.chnop_item_addFirst = &chnop_item_addFirst;
    chnop_item_list.chnop_item_addLast = &chnop_item_addLast;
    chnop_item_list.chnop_item_remove = &chnop_item_remove;
    chnop_item_list.chnop_item_display = &chnop_item_display;
    chnop_item_list.chnop_item_create_node = &chnop_item_create_node;
    chnop_item_list.size = 0;

    return chnop_item_list;
}

/** keyop_item_add item to any position
 */
void keyop_item_add(keyop_item_linked_list *_this, KEYOP_ITEM keyop_item, u8 *devUnitID,
                    u8 *keyinput_board_id, int index, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->keyop_item_addFirst(_this, keyop_item, devUnitID, keyinput_board_id, index);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->keyop_item_addLast(_this, keyop_item, devUnitID, keyinput_board_id, index);
    }
}

/** keyop_item_add item to head
 */
void keyop_item_addFirst(keyop_item_linked_list *_this, KEYOP_ITEM keyop_item, u8 *devUnitID,
                         u8 *keyinput_board_id, int index)
{
    node_keyop_item *newNode = _this->keyop_item_create_node(keyop_item, devUnitID,
                                                             keyinput_board_id, index);
    node_keyop_item *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_keyop_item *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void keyop_item_addLast(keyop_item_linked_list *_this, KEYOP_ITEM keyop_item, u8 *devUnitID,
                        u8 *keyinput_board_id, int index)
{
    node_keyop_item *newNode = _this->keyop_item_create_node(keyop_item, devUnitID,
                                                             keyinput_board_id, index);
    node_keyop_item *head = _this->head;
    node_keyop_item *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_keyop_item *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {

            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && memcmp(head->keyinput_board_id, keyinput_board_id, 12) == 0
                && head->key_index == index) {
                if (head->keyop_item.devId == keyop_item.devId
                    && head->keyop_item.devType == keyop_item.devType
                    && memcmp(head->keyop_item.devUnitID, keyop_item.devUnitID, 12) == 0) {
                    head->keyop_item = keyop_item;
                    return;
                }
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}


/** get item and remove it from any position
 */
void keyop_item_remove(keyop_item_linked_list *_this, u8 *devUnitID, u8 *keyinput_board_id,
                       int index)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_keyop_item *node = _this->head;

    // loop until position
    while (node) {
        if (memcmp(node->devUnitID, devUnitID, 12) == 0
            && memcmp(node->keyinput_board_id, keyinput_board_id, 12) == 0
            && node->key_index == index) {
            node_keyop_item *temp;
            if (node->next != NULL) {
                temp = node;
                node = temp->next;
                node->prev = NULL;
                free(temp);
            } else {
                _this->tail = node->prev;
                free(node);
                node = NULL;
                _this->head = NULL;
            }
            _this->size--;
        } else {
            node = node->next;
        }
    }
}

/** display the items in the list
 */
void keyop_item_display(keyop_item_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_keyop_item *node = _this->head;
        for (i = 0; i < size; i++) {
            node = node->next;
        }
    }
}

/** create a Node
 */
node_keyop_item *keyop_item_create_node(KEYOP_ITEM keyop_item, u8 *devUnitID, u8 *keyinput_board_id,
                                        int index)
{
    node_keyop_item *node = (node_keyop_item *) malloc(sizeof(node_keyop_item));
    memcpy(node->devUnitID, devUnitID, 12);
    memcpy(node->keyinput_board_id, keyinput_board_id, 12);
    node->key_index = index;
    node->keyop_item = keyop_item;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
keyop_item_linked_list keyop_item_create_linked_list()
{
    keyop_item_linked_list keyop_item_list;

    keyop_item_list.head = NULL;
    keyop_item_list.tail = NULL;
    keyop_item_list.keyop_item_add = &keyop_item_add;
    keyop_item_list.keyop_item_addFirst = &keyop_item_addFirst;
    keyop_item_list.keyop_item_addLast = &keyop_item_addLast;
    keyop_item_list.keyop_item_remove = &keyop_item_remove;
    keyop_item_list.keyop_item_display = &keyop_item_display;
    keyop_item_list.keyop_item_create_node = &keyop_item_create_node;
    keyop_item_list.size = 0;

    return keyop_item_list;
}


/** gw_client_add item to any position
 */
void gw_client_add(gw_client_linked_list *_this, struct sockaddr_in sender, u8 *devUnitID,
                   u8 *devPass, u8 *ip, int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->gw_client_addFirst(_this, sender, devUnitID, devPass, ip);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->gw_client_addLast(_this, sender, devUnitID, devPass, ip);
    }
}

/** gw_client_add item to head
 */
void gw_client_addFirst(struct gw_client_linked_list *_this, struct sockaddr_in sender,
                        u8 *devUnitID, u8 *devPass, u8 *ip)
{
    node_gw_client *newNode = _this->gw_client_create_node(sender, devUnitID, devPass, ip);
    node_gw_client *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_gw_client *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void gw_client_addLast(gw_client_linked_list *_this, struct sockaddr_in sender, u8 *devUnitID,
                       u8 *devPass, u8 *ip)
{
    node_gw_client *newNode = _this->gw_client_create_node(sender, devUnitID, devPass, ip);
    node_gw_client *head = _this->head;
    node_gw_client *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_gw_client *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (head->gw_sender.sin_addr.s_addr == sender.sin_addr.s_addr) {
                head->gw_sender = sender;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;
    }
}

/** display the items in the list
 */
void gw_client_display(gw_client_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_gw_client *node = _this->head;
        for (i = 0; i < size; i++) {
            char rcu_ip[16] = {0};
            sprintf(rcu_ip, "%d.%d.%d.%d", node->rcu_ip[0], node->rcu_ip[1], node->rcu_ip[2],
                    node->rcu_ip[3]);
            node = node->next;
        }
    }
}

/** create a Node
 */
node_gw_client *gw_client_create_node(struct sockaddr_in gw_client, u8 *devUnitID, u8 *devPass,
                                      u8 *ip)
{
    node_gw_client *node = (node_gw_client *) malloc(sizeof(node_gw_client));
    memcpy(node->gw_id, devUnitID, 12);
    memcpy(node->gw_pass, devPass, 8);
    memcpy(node->rcu_ip, ip, 4);
    node->gw_sender = gw_client;

    node->prev = NULL;
    node->next = NULL;
    return node;
}

/** create a LinkedList
 */
gw_client_linked_list gw_client_create_linked_list()
{
    gw_client_linked_list gw_client_list;

    gw_client_list.head = NULL;
    gw_client_list.tail = NULL;
    gw_client_list.gw_client_add = &gw_client_add;
    gw_client_list.gw_client_addFirst = &gw_client_addFirst;
    gw_client_list.gw_client_addLast = &gw_client_addLast;
    gw_client_list.gw_client_display = &gw_client_display;
    gw_client_list.gw_client_create_node = &gw_client_create_node;
    gw_client_list.size = 0;

    return gw_client_list;
}


/** app_client_add item to any position
 */
void app_client_add(app_client_linked_list *_this, struct sockaddr_in sender, u8 *app_ip,
                    int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->app_client_addFirst(_this, sender, app_ip);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->app_client_addLast(_this, sender, app_ip);
    }
}

/** app_client_add item to head
 */
void app_client_addFirst(struct app_client_linked_list *_this, struct sockaddr_in sender,
                         u8 *app_ip)
{
    node_app_client *newNode = _this->app_client_create_node(sender, app_ip);
    node_app_client *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_app_client *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void app_client_addLast(app_client_linked_list *_this, struct sockaddr_in sender, u8 *app_ip)
{
    node_app_client *newNode = _this->app_client_create_node(sender, app_ip);
    node_app_client *head = _this->head;
    node_app_client *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_app_client *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (head->app_sender.sin_addr.s_addr == sender.sin_addr.s_addr) {
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}

/** display the items in the list
 */
void app_client_display(app_client_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_app_client *node = _this->head;
        for (i = 0; i < size; i++) {
            node = node->next;
        }
    }
}

/** create a Node
 */
node_app_client *app_client_create_node(struct sockaddr_in app_client, u8 *app_ip)
{
    node_app_client *node = (node_app_client *) malloc(sizeof(node_app_client));

    node->app_sender = app_client;
    memcpy(node->app_ip, app_ip, 4);

    node->prev = NULL;
    node->next = NULL;
    return node;
}

/** create a LinkedList
 */
app_client_linked_list app_client_create_linked_list()
{
    app_client_linked_list app_client_list;

    app_client_list.head = NULL;
    app_client_list.tail = NULL;
    app_client_list.app_client_add = &app_client_add;
    app_client_list.app_client_addFirst = &app_client_addFirst;
    app_client_list.app_client_addLast = &app_client_addLast;
    app_client_list.app_client_display = &app_client_display;
    app_client_list.app_client_create_node = &app_client_create_node;
    app_client_list.size = 0;

    return app_client_list;
}


/** udp_msg_queue_add item to any position
 */
void udp_msg_queue_add(udp_msg_queue_linked_list *_this, u8 *devUnitID, int cmd, int id, int flag,
                       int position)
{
    // index out of list size
    if (position > _this->size) {
        return;
    }
    // ware_add to head
    if (position == 0) {
        _this->udp_msg_queue_addFirst(_this, devUnitID, cmd, id, flag);
    } else if (position == _this->size) {
        // ware_add to tail
        _this->udp_msg_queue_addLast(_this, devUnitID, cmd, id, flag);
    }
}

/** udp_msg_queue_add item to head
 */
void udp_msg_queue_addFirst(struct udp_msg_queue_linked_list *_this, u8 *devUnitID, int cmd, int id,
                            int flag)
{
    node_udp_msg_queue *newNode = _this->udp_msg_queue_create_node(devUnitID, cmd, id, flag);
    node_udp_msg_queue *head = _this->head;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_udp_msg_queue *last = _this->tail;
        if (last == NULL) // only head node
            last = head;
        newNode->next = head;
        head->prev = newNode;
        _this->head = newNode;
        _this->tail = last;
    }

    _this->size++;
}

/** ware_add item to tail
 */
void udp_msg_queue_addLast(udp_msg_queue_linked_list *_this, u8 *devUnitID, int cmd, int id,
                           int flag)
{
    node_udp_msg_queue *newNode = _this->udp_msg_queue_create_node(devUnitID, cmd, id, flag);
    node_udp_msg_queue *head = _this->head;
    node_udp_msg_queue *tail = _this->tail;
    // list is empty
    if (head == NULL)
        _this->head = newNode;
    else { // has item(s)
        node_udp_msg_queue *lastNode = tail;
        if (tail == NULL) // only head node
            lastNode = head;

        while (head) {
            if (memcmp(head->devUnitID, devUnitID, 12) == 0
                && head->cmd == cmd
                && head->id == id) {
                head->flag = flag;
                return;
            }
            head = head->next;
        }

        lastNode->next = newNode;
        newNode->prev = lastNode;
        _this->tail = newNode;
        _this->size++;

    }
}


/** get item and remove it from any position
 */
void udp_msg_queue_remove(udp_msg_queue_linked_list *_this, u8 *devUnitID, int cmd)
{
    // list is empty
    if (_this->size == 0) {
        return;
    }
    node_udp_msg_queue *node = _this->head;
    node_udp_msg_queue *prev;
    node_udp_msg_queue *next;
    int i = 0;

    // loop until position
    while (memcpy(node->devUnitID, devUnitID, 12) != 0 || node->cmd != cmd) {
        node = node->next;
        i++;
    }

    // remove node from list
    prev = node->prev;
    next = node->next;
    prev->next = next;
    next->prev = prev;
    free(node);
    _this->size--;

}

/** display the items in the list
 */
void udp_msg_queue_display(udp_msg_queue_linked_list *_this)
{
    int i, size = _this->size;
    if (size == 0)
        return;
    else {
        node_udp_msg_queue *node = _this->head;
        for (i = 0; i < size; i++) {
            node = node->next;
        }
    }
}

/** create a Node
 */
node_udp_msg_queue *udp_msg_queue_create_node(u8 *uid, int cmd, int id, int flag)
{
    node_udp_msg_queue *node = (node_udp_msg_queue *) malloc(sizeof(node_udp_msg_queue));

    memcpy(node->devUnitID, uid, 12);
    node->cmd = cmd;
    node->id = id;
    node->flag = flag;
    node->prev = NULL;
    node->next = NULL;
    return node;
}


/** create a LinkedList
 */
udp_msg_queue_linked_list udp_msg_queue_create_linked_list()
{
    udp_msg_queue_linked_list udp_msg_queue_list;

    udp_msg_queue_list.head = NULL;
    udp_msg_queue_list.tail = NULL;
    udp_msg_queue_list.udp_msg_queue_add = &udp_msg_queue_add;
    udp_msg_queue_list.udp_msg_queue_addFirst = &udp_msg_queue_addFirst;
    udp_msg_queue_list.udp_msg_queue_addLast = &udp_msg_queue_addLast;
    udp_msg_queue_list.udp_msg_queue_remove = &udp_msg_queue_remove;
    udp_msg_queue_list.udp_msg_queue_display = &udp_msg_queue_display;
    udp_msg_queue_list.udp_msg_queue_create_node = &udp_msg_queue_create_node;
    udp_msg_queue_list.size = 0;

    return udp_msg_queue_list;
}
