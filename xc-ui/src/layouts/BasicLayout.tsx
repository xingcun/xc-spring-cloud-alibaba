/**
 * Ant Design Pro v4 use `@ant-design/pro-layout` to handle Layout.
 * You can view component api by:
 * https://github.com/ant-design/ant-design-pro-layout
 */

import { ConnectState, ConnectProps } from '@/models/connect';
import RightContent from '@/components/GlobalHeader/RightContent';
import { connect } from 'dva';
import React, { useState } from 'react';
import logo from '../assets/logo.svg';
import Authorized from '@/utils/Authorized';
import { formatMessage } from 'umi-plugin-react/locale';
import { isAntDesignPro } from '@/utils/utils';
import {
  BasicLayout as ProLayoutComponents,
  BasicLayoutProps as ProLayoutComponentsProps,
  MenuDataItem,
  Settings,
} from '@ant-design/pro-layout';
import Link from 'umi/link';
import GlobalFooter from "@/components/GlobalFooter";
import { Icon } from "antd";
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
/**
 * use Authorized check all menu item
 */

const menuDataRender = (menuList: MenuDataItem[]): MenuDataItem[] => {
  return menuList.map(item => {
    const localItem = { ...item, children: item.children ? menuDataRender(item.children) : [] };
    return Authorized.check(item.authority, localItem, null) as MenuDataItem;
  });
};

const links = [
  {
    key: 'help',
    title: 'ant design pro',
    href: 'https://pro.ant.design/index-cn',
    blankTarget: true
  },
  {
    key: 'privacy',
    title: 'ant design',
    href: 'https://ant.design/index-cn',
    blankTarget: true
  },
  {
    key: 'terms',
    title: 'xc后台架构',
    href: 'https://github.com/xingcun/xc-spring-cloud-alibaba',
    blankTarget: true
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

const BasicLayout: React.FC<BasicLayoutProps> = props => {
  const { dispatch, children, settings } = props;
  /**
   * constructor
   */

  useState(() => {
    if (dispatch) {
      dispatch({
        type: 'user/getLoginUser',
      });
      dispatch({
        type: 'settings/getSetting',
      });
    }
  });

  /**
   * init variables
   */
  const handleMenuCollapse = (payload: boolean) =>
    dispatch &&
    dispatch({
      type: 'global/changeLayoutCollapsed',
      payload,
    });

  return (
    <ProLayoutComponents
      logo={logo}
      onCollapse={handleMenuCollapse}
      menuItemRender={(menuItemProps, defaultDom) => {
        return  !menuItemProps.path.startsWith('http')?(
          <Link to={menuItemProps.path}>{defaultDom}</Link>
          ):(<div>{defaultDom}</div>);
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
      {...props}
      {...settings}
    >
      {children}
    </ProLayoutComponents>
  );
};

export default connect(({ global, settings }: ConnectState) => ({
  collapsed: global.collapsed,
  settings,
}))(BasicLayout);
