import React, { Component, Fragment } from 'react';
import { formatMessage, FormattedMessage } from 'umi-plugin-react/locale';
import { Form, Input, Upload, Select, Button, message,Alert } from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { connect } from 'dva';
import styles from './BaseView.less';
const FormItem = Form.Item;
const { Option } = Select;

// 输出结果
const AvatarView = ({ remark,result }) => (
  <Fragment>
    <div>
      {
        remark && (
          <Alert message={remark} type="info" />
        )
      }

    </div>
    <div>
      <Input.TextArea
        placeholder={'输出结果'}
        rows={10}
        value={result?JSON.stringify(result):''}

      />
    </div>
  </Fragment>
);


@connect(
  ({
     testModel,
     loading,
   }) => ({
    testModel,
    loading
  }),
)
class TestBaseView extends Component<FormComponentProps> {
  view: HTMLDivElement | undefined;
  componentDidMount() {
    this.setBaseInfo();
  }

  setBaseInfo = () => {
    const {  form } = this.props;
  };


  getViewDom = (ref: HTMLDivElement) => {
    this.view = ref;
  };

  handlerSubmit = (event: Event) => {
    event.preventDefault();
    const { form ,control} = this.props;


    form.validateFields((err, values) => {
      if (!err) {
        this.props.testModel.result={'状态':"正在努力加载中"};
        control(values);
      //  message.success(formatMessage({ id: 'account-settings.basic.update.success' }));
      }
    });
  };


  render() {
    const {
      form: { getFieldDecorator },
      remark,
      listInput=[],
    } = this.props;
    return (
      <div className={styles.baseView} ref={this.getViewDom}>
        <div className={styles.left}>
          <Form layout="vertical" hideRequiredMark>
            {
              listInput.map((item, index) =>{
                const {id,options}=item;
                return (
                  <FormItem label={item.label} key={id}>
                    {getFieldDecorator(id, options)(<Input />)}
                  </FormItem>
                );
              })
            }

            <Button type="primary" onClick={this.handlerSubmit}>
              测试
            </Button>
          </Form>
        </div>
        <div className={styles.right}>
          <AvatarView remark={remark} result={this.props.testModel.result}/>
        </div>
      </div>
    );
  }
}

export default Form.create<FormComponentProps>()(TestBaseView);
