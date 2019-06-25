import React from 'react';
import { formatMessage, FormattedMessage } from 'umi-plugin-react/locale';
import { Button } from 'antd';
import Link from 'umi/link';
import Result from './Result';
import styles from './style.less';
import { RouteChildrenProps } from 'react-router';

const actions = (
  <div className={styles.actions}>
   {/* <a href="">
      <Button size="large" type="primary">
        <FormattedMessage id="user-register-result.register-result.view-mailbox" />
      </Button>
    </a>*/}
    <Link to="/">
      <Button size="large">
        <FormattedMessage id="user-register.login.login" />
      </Button>
    </Link>
  </div>
);

const RegisterResult: React.SFC<RouteChildrenProps> = ({ location }) => (
  <Result
    className={styles.registerResult}
    type="success"
    title={
      <div className={styles.title}>
        <FormattedMessage
          id="user-register-result.register-result.msg"
          values={{ email: location.state ? location.state.account : 'AntDesign@example.com' }}
        />
      </div>
    }
   /* description={formatMessage({ id: 'user-register-result.register-result.activation-email' })}*/
    actions={actions}
    style={{ marginTop: 56 }}
  />
);

export default RegisterResult;
