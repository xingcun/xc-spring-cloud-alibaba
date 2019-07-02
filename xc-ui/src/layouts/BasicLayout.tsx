/**
 * Ant Design Pro v4 use `@ant-design/pro-layout` to handle Layout.
 * You can view component api by:
 * https://github.com/ant-design/ant-design-pro-layout
 */

import { ConnectState, ConnectProps } from '@/models/connect';
import RightContent from '@/components/GlobalHeader/RightContent';
import { connect } from 'dva';
import React, { PureComponent } from 'react';
import logo from '../assets/logo.svg';
import Authorized from '@/utils/Authorized';
import { formatMessage } from 'umi-plugin-react/locale';
import { Tabs, Dropdown, Menu, Icon } from 'antd';

import {
  BasicLayout as ProLayoutComponents,
  BasicLayoutProps as ProLayoutComponentsProps,
  MenuDataItem,
  Settings,
} from '@ant-design/pro-layout';
import GlobalFooter from '@/components/GlobalFooter';
import pathToRegexp from 'path-to-regexp';
import router from 'umi/router';
import { Route } from 'react-router-dom';

export interface BasicLayoutProps extends ProLayoutComponentsProps, ConnectProps {
  breadcrumbNameMap: {
    [path: string]: MenuDataItem;
  };
  settings: Settings;
}
export type BasicLayoutContext = { [K in 'location']: BasicLayoutProps[K] } & {
  breadcrumbNameMap: {
    [path: string]: MenuDataItem;
  };
};

const { TabPane } = Tabs;

/**
 * use Authorized check all menu item
 */

const menuDataRender = (menuList: MenuDataItem[]): MenuDataItem[] => {
  return menuList.map(item => {
    // console.log(item)
    const localItem = { ...item, children: item.children ? menuDataRender(item.children) : [] };
    return Authorized.check(item.authority, localItem, null) as MenuDataItem;
  });
};

const links = [
  {
    key: 'help',
    title: 'ant design pro',
    href: 'https://pro.ant.design/index-cn',
    blankTarget: true,
  },
  {
    key: 'privacy',
    title: 'ant design',
    href: 'https://ant.design/index-cn',
    blankTarget: true,
  },
  {
    key: 'terms',
    title: 'xc后台架构',
    href: 'https://github.com/xingcun/xc-spring-cloud-alibaba',
    blankTarget: true,
  },
];

const copyright = (
  <>
    Copyright <Icon type="copyright" /> 2019 蚂蚁金服体验技术部出品
  </>
);

const footerRender: BasicLayoutProps['footerRender'] = (_, defaultDom) => {
  /*  if (!isAntDesignPro()) {
    return defaultDom;
  }*/
  return (
    <>
      <GlobalFooter links={links} copyright={copyright} />
    </>
  );
};

class BasicLayout extends PureComponent<BasicLayoutProps, any> {
  constructor(props: any) {
    super(props);
    const { dispatch } = props;
    if (dispatch) {
      dispatch({
        type: 'user/getLoginUser',
      });
      dispatch({
        type: 'settings/getSetting',
      });
    }
    const { routes } = props.route,
      routeKey = props.location.pathname; // routeKey 为设置首页设置 试试 '/dashboard/analysis' 或其他key值
    const tabLists = this.updateTree(routes);
    let tabList: any[] = [];
    tabLists.map(v => {
      if (v.key === routeKey) {
        if (tabList.length === 0) {
          v.closable = false;
          tabList.push(v);
        }
      }
    });
    this.state = {
      tabLists: tabLists,
      tabList: tabList,
      tabListKey: [routeKey],
      activeKey: routeKey,
      routeKey,
    };

    // this.getPageTitle = memoizeOne(this.getPageTitle);
    // this.matchParamsPath = memoizeOne(this.matchParamsPath, isEqual);
  }

  getPageTitle = (pathname: any, breadcrumbNameMap: any) => {
    const currRouterData = this.matchParamsPath(pathname, breadcrumbNameMap);

    if (!currRouterData) {
      return 'Ant Tabs';
    }
    const pageName = formatMessage({
      id: currRouterData.locale || currRouterData.name,
      defaultMessage: currRouterData.name,
    });

    return `${pageName} - Ant Tabs`;
  };

  matchParamsPath = (pathname: any, breadcrumbNameMap: any) => {
    const pathKey = Object.keys(breadcrumbNameMap).find(key => pathToRegexp(key).test(pathname));
    return pathKey ? breadcrumbNameMap[pathKey] : '';
  };

  updateTree = (data: any) => {
    const treeData = data;
    let treeList: any[] = [];
    // 递归获取树列表
    const getTreeList = (data: any, name: any) => {
      data.forEach((node: any) => {
        if (node.routes && node.routes.length > 0) {
          //!node.hideChildrenInMenu &&
          getTreeList(node.routes, (name.length > 0 ? name + '.' : '') + node.name);
        } else {
          treeList.push({
            tab: (name.length > 0 ? name + '.' : '') + node.name,
            key: node.path,
            locale: node.locale,
            closable: true,
            content: node.component,
          });
        }
      });
    };
    getTreeList(treeData, 'menu');
    return treeList;
  };

  // 切换 tab页 router.push(key);
  onChange = (key: any) => {
    this.setState({ activeKey: key });
    router.push(key);
  };

  onEdit = (targetKey: any, action: any) => {
    this[action](targetKey);
  };

  remove = (targetKey: any) => {
    let { activeKey } = this.state;
    let lastIndex = -1;
    this.state.tabList.forEach((pane: any, i: any) => {
      if (pane.key === targetKey) {
        lastIndex = i - 1;
      }
    });
    let tabList: any[] = [],
      tabListKey: any = [];
    this.state.tabList.map((pane: any) => {
      if (pane.key !== targetKey) {
        tabList.push(pane);
        tabListKey.push(pane.key);
      }
    });
    if (lastIndex >= 0 && activeKey === targetKey) {
      activeKey = tabList[lastIndex].key;
    }
    router.push(activeKey);

    this.setState({ tabList, activeKey, tabListKey });
  };

  onHandlePage = (key: any) => {
    //点击左侧菜单
    // const { menuData } = this.props;

    //   const tabLists = this.updateTreeList(menuData);
    const { tabLists, tabListKey, tabList } = this.state;
    router.push(key);
    this.setState({
      activeKey: key,
    });

    tabLists.map((v: any) => {
      if (v.key === key) {
        if (tabList.length === 0) {
          v.closable = false;
          this.setState({
            tabList: [...tabList, v],
          });
        } else {
          if (!tabListKey.includes(v.key)) {
            if (tabList.length === 1) {
              tabList[0].closable = true;
            }
            this.setState({
              tabList: [...tabList, v],
              tabListKey: [...tabListKey, v.key],
            });
          }
        }
      }
    });

    // this.setState({
    //   tabListKey:this.state.tabList.map((va)=>va.key)
    // })
  };

  updateTreeList = (data: any) => {
    const treeData = data;
    let treeList: any[] = [];
    // 递归获取树列表
    const getTreeList = (data: any) => {
      if (data) {
        data.forEach((node: any) => {
          if (!node.level) {
            treeList.push({
              tab: node.name,
              key: node.path,
              locale: node.locale,
              closable: true,
              content: node.component,
            });
          }
          if (node.children && node.children.length > 0) {
            //!node.hideChildrenInMenu &&
            getTreeList(node.children);
          }
        });
      }
    };
    getTreeList(treeData);
    return treeList;
  };

  onClickHover = (e: any) => {
    // message.info(`Click on item ${key}`);
    let { key } = e,
      { activeKey, tabList, tabListKey, routeKey } = this.state;

    if (key === '1') {
      tabList = tabList.filter((v: any) => v.key !== activeKey || v.key === routeKey);
      tabListKey = tabListKey.filter((v: any) => v !== activeKey || v === routeKey);
      this.setState({
        activeKey: routeKey,
        tabList,
        tabListKey,
      });
    } else if (key === '2') {
      tabList = tabList.filter((v: any) => v.key === activeKey || v.key === routeKey);
      tabListKey = tabListKey.filter((v: any) => v === activeKey || v === routeKey);
      this.setState({
        activeKey,
        tabList,
        tabListKey,
      });
    } else if (key === '3') {
      tabList = tabList.filter((v: any) => v.key === routeKey);
      tabListKey = tabListKey.filter((v: any) => v === routeKey);
      this.setState({
        activeKey: routeKey,
        tabList,
        tabListKey,
      });
    }
  };

  handleMenuCollapse = (payload: boolean) => {
    const { dispatch } = this.props;
    dispatch &&
      dispatch({
        type: 'global/changeLayoutCollapsed',
        payload,
      });
  };

  render() {
    const menu = (
      <Menu onClick={this.onClickHover}>
        <Menu.Item key="1">关闭当前标签页</Menu.Item>
        <Menu.Item key="2">关闭其他标签页</Menu.Item>
        <Menu.Item key="3">关闭全部标签页</Menu.Item>
      </Menu>
    );
    const operations = (
      <Dropdown overlay={menu}>
        <a className="ant-dropdown-link" href="#">
          更多操作
          <Icon type="down" />
        </a>
      </Dropdown>
    );
    const { children, settings } = this.props;
    let { activeKey, routeKey } = this.state;

    return (
      <ProLayoutComponents
        logo={logo}
        onCollapse={this.handleMenuCollapse}
        menuItemRender={(menuItemProps, defaultDom) => {
          return !menuItemProps.path.startsWith('http') ? (
            <div
              onClick={() => {
                this.onHandlePage(menuItemProps.path);
              }}
            >
              {defaultDom}
            </div>
          ) : (
            <div>{defaultDom}</div>
          );
        }}
        breadcrumbRender={(routers = []) => {
          return [
            {
              path: '/',
              breadcrumbName: formatMessage({
                id: 'menu.home',
                defaultMessage: 'Home',
              }),
            },
            ...routers,
          ];
        }}
        footerRender={footerRender}
        menuDataRender={menuDataRender}
        formatMessage={formatMessage}
        rightContentRender={rightProps => <RightContent {...rightProps} />}
        {...this.props}
        {...settings}
      >
        <Tabs
          // className={styles.tabs}
          activeKey={activeKey}
          onChange={this.onChange}
          tabBarExtraContent={operations}
          tabBarStyle={{ background: '#fff' }}
          tabPosition="top"
          tabBarGutter={-1}
          hideAdd
          type="editable-card"
          onEdit={this.onEdit}
        >
          {this.state.tabList.map((item: any) => (
            <TabPane tab={formatMessage({ id: item.tab })} key={item.key} closable={item.closable}>
              <Route key={item.key} path={item.path} component={item.content} exact={item.exact} />
            </TabPane>
          ))}
        </Tabs>

        {/*  {children}*/}
      </ProLayoutComponents>
    );
  }
}

export default connect(({ global, settings }: ConnectState) => ({
  collapsed: global.collapsed,
  settings,
}))(BasicLayout);
