import React, { Component } from 'react';
import { Input, Select, Button, DatePicker, Form, Modal, Steps, Radio } from 'antd';
import { TableListItem } from '../data';
import { FormComponentProps } from 'antd/es/form';

export type IFormValsType = {
  target?: string;
  template?: string;
  type?: string;
  time?: string;
  frequency?: string;
} & Partial<TableListItem>;

export interface UpdateFormProps extends FormComponentProps {
  handleUpdateModalVisible: (flag?: boolean, formVals?: IFormValsType) => void;
  handleUpdate: (values: IFormValsType) => void;
  updateModalVisible: boolean;
  values: Partial<TableListItem>;
}
const FormItem = Form.Item;
const { Step } = Steps;
const { TextArea } = Input;
const { Option } = Select;
const RadioGroup = Radio.Group;

export interface UpdateFormState {
  formVals: IFormValsType;
  currentStep: number;
}

class UpdateForm extends Component<UpdateFormProps, UpdateFormState> {
  static defaultProps = {
    handleUpdate: () => {},
    handleUpdateModalVisible: () => {},
    values: {},
  };
  formLayout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 13 },
  };
  constructor(props: UpdateFormProps) {
    super(props);

    this.state = {
    };
  }

  handleNext = () => {
    const { form, handleUpdate,values } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      const formVals = { id:values.id, ...fieldsValue };
      this.setState(
        {
          formVals,
        },
        () => {
            handleUpdate(formVals);
        },
      );
    });
  };


  renderContent = (currentStep: number, formVals: IFormValsType) => {
    const { form } = this.props;
    return [
      <FormItem key="userName" {...this.formLayout} label="用户名称">
        {form.getFieldDecorator('userName', {
          rules: [{ required: true, message: '请输入用户名！' }],
          initialValue: formVals.userName,
        })(<Input placeholder="请输入" />)}
      </FormItem>,
      <FormItem key="mobile" {...this.formLayout} label="手机号">
        {form.getFieldDecorator('mobile', {
          rules: [{ required: true, message: '请输入正确的手机号！' ,pattern: '^[1][3,4,5,6,7,8,9][0-9]{9}$'}],
          initialValue: formVals.mobile,
        })(<Input placeholder="请输入" />)}
      </FormItem>,
      <FormItem key="password" {...this.formLayout} label="密码">
        {form.getFieldDecorator('password', {
          rules: [{ validator:(rule, value, callback)=>{
              if(!form.getFieldValue('id') && !value){
                callback('请输入密码！')
              }else{
                callback();
              }
            }, message: '请输入密码！' }],
          initialValue: formVals.password,
        })(<Input placeholder="请输入" />)}
      </FormItem>,
      <FormItem key="nickName" {...this.formLayout} label="昵称">
        {form.getFieldDecorator('nickName',{
          initialValue: formVals.nickName
        })(<Input placeholder="请输入" />)}
      </FormItem>,

      <FormItem key="source" {...this.formLayout} label="注册来源" >
        {form.getFieldDecorator('source', {
          initialValue: formVals.source || 0,
        })(
          <Select style={{ width: '100%' }}>
            <Option value={0}>后台</Option>
            <Option value={1}>APP</Option>
          </Select>,
        )}
      </FormItem>,

      <FormItem key="valid" {...this.formLayout} label="有效" hasFeedback>
        {form.getFieldDecorator('valid', {
          rules: [{ required: true, message: '请选择是否有效！' }],
          initialValue: formVals.valid,
        })(<Radio.Group >
          <Radio.Button value={true}>有效</Radio.Button>
          <Radio.Button value={false}>无效</Radio.Button>
        </Radio.Group>)}
      </FormItem>,
    ];
  };

  renderFooter = () => {
    const { handleUpdateModalVisible, values } = this.props;

    return [
      <Button key="cancel" onClick={() => handleUpdateModalVisible(false, values)}>
        取消
      </Button>,
      <Button key="forward" type="primary" onClick={() => this.handleNext()}>
        完成
      </Button>,
    ];
  };

  render() {
    const { updateModalVisible, handleUpdateModalVisible, values } = this.props;
    const { currentStep, formVals } = this.state;
    return (
      <Modal
        width={800}
        bodyStyle={{ padding: '32px 40px 48px' }}
        destroyOnClose
        title={values && values.id ?'修改用户':'添加用户'}
        visible={updateModalVisible}
        footer={this.renderFooter(currentStep)}
        onCancel={() => handleUpdateModalVisible(false, values)}
        afterClose={() => handleUpdateModalVisible()}
      >
        {this.renderContent(currentStep, values)}
      </Modal>
    );
  }
}


export default Form.create<UpdateFormProps>()(UpdateForm);
