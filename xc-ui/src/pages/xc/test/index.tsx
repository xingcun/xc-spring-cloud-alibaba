import {Button, Card, Form, Menu} from 'antd';
import React, { createElement } from 'react';
import {GridContent, PageHeaderWrapper} from "@ant-design/pro-layout";
import styles from "./style.less";
import BaseView from "./base";
import { Dispatch } from 'redux';
import { connect } from 'dva';
import {FormComponentProps} from "antd/es/form";
import {IStateType} from "@/pages/user/login/model";

const { Item } = Menu;

@connect(
  ({
     testModel,
     loading,
   }) => ({
    testModel,
    loading
  }),
)

class TestComponent extends React.Component<any, any> {
  main: HTMLDivElement | undefined;
  constructor(props: any) {
    super(props);
    const menuMap = {
      base: "测试dubbo服务",
      testCache: "检测ignite cache",
      testLock: '测试ignite同步锁',
      testExecutor: "测试线程池执行",
      testFeign: "测试Feign",
      testFeignFactory: '测试feign熔断工厂',
      testLocalEvent: "测试event本地",
      testRemoteEvent: '测试event全局',
      testMqEvent: '测试Stream mq消息',
    };
    //testRemoteEvent,testLocalEvent, testDubbo,testCache ,testLock,testFeignFactory,testFeign,testExecutor,testMqEvent
    this.state = {
      mode: 'inline',
      menuMap,
      selectKey: 'base',
      listInput: [],
      dispatchType: '',
      showRemark:''
    };
  }
  componentDidMount() {
    this.selectKey(this.state.selectKey);
  }
  getMenu = () => {

    const { menuMap } = this.state;
    return Object.keys(menuMap).map(item => <Item key={item}>{menuMap[item]}</Item>);
  };

  selectKey = (key: any) => {

    const { dispatch } = this.props;
    dispatch({
      type: 'testModel/save',
      payload: {},
    });
    let listInput=[
      {
        id: 'msg',
        label: '消息内容',
        options: {
          rules: [
            {
              required: true,
              message:'消息必填',
            },
          ],
        }
      },
    ],dispatchType='',showRemark='';
    switch (key) {
      case 'base':
        dispatchType = 'testModel/testDubbo';
        break;
      case 'testCache':
        dispatchType = 'testModel/testCache';
        showRemark = '测试ignite缓存功能,可以后台看到service与admin各自输出cache内容';
        break;
      case 'testLock':
        dispatchType = 'testModel/testLock';
        showRemark = '测试ignite同步锁功能,默认获取加锁时间为3秒',
        listInput=[
            {
              id: 'time',
              label: '时间毫秒',
              options: {
                rules: [
                  {
                    required: true,
                    message:'时间必填',
                  },
                ],
              }
            },
          ];
        break;
      case 'testExecutor':
        dispatchType = 'testModel/testExecutor';
        showRemark = '测试Executor线程池功能';
        break;
      case 'testFeign':
        dispatchType = 'testModel/testFeign';
        showRemark = '测试feign功能,会有机率触发Hystrix';
        break;
      case 'testFeignFactory':
        dispatchType = 'testModel/testFeignFactory';
        showRemark = '测试feign功能,会有机率触发使用HystrixFactory进行熔断处理';
        break;
      case 'testLocalEvent':
        dispatchType = 'testModel/testLocalEvent';
        showRemark = '测试Event功能,此event事件只通知本地，即项目调用put方,service,如admin是收到不event事件';
        break;
      case 'testRemoteEvent':
        listInput=[];
        dispatchType = 'testModel/testRemoteEvent';
        showRemark = '测试Event功能,此event事件通知全局，能过mq进行消息传递，所有实现相同event的都能收到消息,如service,admin';
        break;
      case 'testMqEvent':
        listInput=[
          {
            id: 'name',
            label: '名称',
            options: {
              rules: [
                {
                  required: true,
                  message:'名称必填',
                },
              ],
            }
          },
          {
            id: 'age',
            label: '年龄',
            options: {
              rules: [
                {
                  required: true,
                  message:'年龄必填',
                },
              ],
            }
          },
        ];

        dispatchType = 'testModel/testMqEvent';
        showRemark = '测试mq接收消息功能,此service发送mq消息,admin进行接收';
        break;
      default:
        break;
    }
    this.setState({
      selectKey: key,
      listInput: listInput,
      dispatchType: dispatchType,
      showRemark: showRemark
    });
  };

  getRightTitle = () => {
    const { selectKey, menuMap } = this.state;
    return menuMap[selectKey];
  };

  renderChildren = () => {
   return <BaseView listInput={this.state.listInput} control={this.goToControl} remark={this.state.showRemark}/>;

  };

  goToControl = (data: any)=> {

    const { dispatchType } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: dispatchType,
      payload: data
    });


  }

  render() {
    const { mode, selectKey } = this.state;
    return (
      <GridContent>
        <div
          className={styles.main}
          ref={ref => {
            if (ref) {
              this.main = ref;
            }
          }}
        >
          <div className={styles.leftMenu}>
            <Menu
              mode={mode}
              selectedKeys={[selectKey]}
              onClick={({ key }) => this.selectKey(key )}
            >
              {this.getMenu()}
            </Menu>
          </div>
          <div className={styles.right}>
            <div className={styles.title}>{this.getRightTitle()}</div>
            {this.renderChildren()}
          </div>
        </div>
      </GridContent>
    );
  }

}

export default TestComponent;
