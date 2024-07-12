# Page

## 健康干预接口

<mark style="color:green;">`POST`</mark> `/health`

<

根据请求参数（请求时间和userId）：

1. 获取最新一条干预记录数据，拿到干预周期
2. 如果没有记录，说明是第一次请求，以请求时间为准，返回相关数据
3. 如果请求时间在干预周期内，说明是在干预周期内发起请求的，直接返回干预开始时间的检查数据
4. 如果请求时间>干预周期，说明是上一次的干预周期，以请求时间为准，生成新数据
5. 返回数据之前，需要插入一条干预记录，供获取检查列表使用
6. 返回打卡任务

\>

**Headers**

| Name          | Value              |
| ------------- | ------------------ |
| Content-Type  | `application/json` |
| Authorization | `Bearer <token>`   |

**Body**

| Name   | Type   | Description |
| ------ | ------ | ----------- |
| today  | string | 今天请求日期      |
| userId | string | 用户Id        |

**Response**

| Name                | Type   | Description |
| ------------------- | ------ | ----------- |
| medicalOrganization | string | 干预机构        |
| startDate           | string | 干预周期-开始时间   |
| endDate             | string | 干预周期-结束时间   |
| healthAdvice        | json   | 健康建议        |

{% tabs %}
{% tab title="200" %}
```json
{
  "id": 1,
  "name": "John",
  "age": 30
}
```
{% endtab %}

{% tab title="400" %}
```json
{
  "error": "Invalid request"
}
```
{% endtab %}
{% endtabs %}



## 获取健康干预列表

GET `/users`

<

1\. 根据用户名获取干预数据

\>

**Headers**

| Name          | Value              |
| ------------- | ------------------ |
| Content-Type  | `application/json` |
| Authorization | `Bearer <token>`   |

**Body**

| Name     | Type   | Description      |
| -------- | ------ | ---------------- |
| username | string | Name of the user |

**Response**



| Name                | Type | Description |
| ------------------- | ---- | ----------- |
| medicalOrganization |      |             |
| clockDays           |      |             |
| status              |      |             |
| startDate           |      |             |
| endDate             |      |             |

{% tabs %}
{% tab title="200" %}
```json
{
  "id": 1,
  "name": "John",
  "age": 30
}
```
{% endtab %}

{% tab title="400" %}
```json
{
  "error": "Invalid request"
}
```
{% endtab %}
{% endtabs %}

## 打卡干预任务接口

## 健康任务

1. 根据请求时间+userId
   1. 获取任务记录表中的周期时间，如果请求时间属于\[周期开始时间，周期结束时间]，说明是在周期内无需生成；否则：
   2. 根据用户信息判断是否生成高血压、糖尿病任务；
   3. 根据效果问卷规则是否生成效果问卷；效果问卷内容来源，<mark style="color:blue;">**需要从素问拿到主诉分词进行填充及逻辑处理；**</mark>

## 填写健康任务

1. 根据userId+问卷code
   1. 获取最新一条任务记录；
      1. 如果是高血压、糖药病任务，填写完之后，需要生成相关结果外，还需把血压数值填写到统计记录表中；
      2. 如果是效果问卷，如果超过3天未填写，不展示；但由系统去填写。这里需要维护状态字段进行区分；

## 任务记录

1. 根据userId获取状态是已填写的任务记录

## 任务详情

1. 根据userId和任务类型/code获取任务的详情















