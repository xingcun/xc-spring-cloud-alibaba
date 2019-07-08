import React, { Component } from 'react';
import { Input, Select, Button, DatePicker, Form, Modal, Steps, Radio } from 'antd';
import { TableListItem } from '../data';
import { FormComponentProps } from 'antd/es/form';
import moment from 'moment';


export interface UpdateFormProps extends FormComponentProps {
  handleUpdateModalVisible: (flag?: boolean, formVals?: any) => void;
  handleUpdate: (values: any) => void;
  updateModalVisible: boolean;
  values: Partial<TableListItem>;
}
const FormItem = Form.Item;

export interface UpdateFormState {
  formVals: any;
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
      let formVals = { id:values.id, ...fieldsValue };
      if(formVals.param){
        formVals.param = JSON.parse(formVals.param);
      }
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


  renderContent = (currentStep: number, formVals: any) => {
    const { form } = this.props;
    return [
      <FormItem key="name" {...this.formLayout} label="工作名">
        {form.getFieldDecorator('name', {
          rules: [{ required: true, message: '请输入工作名！' }],
          initialValue: formVals.name,
        })(<Input placeholder="请输入" />)}
      </FormItem>,
      <FormItem key="cron" {...this.formLayout} label="cron">
        {form.getFieldDecorator('cron', {
          rules: [{ validator:(rule, value, callback)=>{
              if(!form.getFieldValue('startDate') && !value){
                callback('请输入cron！')
              }else{
                callback();
              }
            }, message: '请输入cron！' }],
          initialValue: formVals.cron,
        })(<Input placeholder="请输入" />)}
      </FormItem>,
      <FormItem key="startDate" {...this.formLayout} label="启动时间">
        {form.getFieldDecorator('startDate',{
          rules: [{ validator:(rule, value, callback)=>{
              if(!form.getFieldValue('cron') && !value){
                callback('请输入启动时间！')
              }else{
                if(value && value < moment().endOf('second') ){
                  callback('启动时间不能少于当前时间！')
                }else{
                  callback();
                }

              }
            }, message: '请输入启动时间！' }],
          initialValue: formVals.startDate?moment(formVals.startDate):null
        })( <DatePicker
          showTime={true}
          disabledDate={(current)=>{
              // Can not select days before today and today
            return current && current < moment().endOf('second');
          }}
          style={{ width: '100%' }}
          placeholder="请输入启动日期与cron二选一"
          format={'YYYY-MM-DD HH:mm:ss'}
        />)}
      </FormItem>,
      <FormItem key="isLocalProject" {...this.formLayout} label="是否job服务内运行" hasFeedback>
        {form.getFieldDecorator('isLocalProject', {
          rules: [{ required: true, message: '请选择是否启动！' }],
          initialValue: formVals.isLocalProject || false,
        })(<Radio.Group >
          <Radio.Button value={false}>否</Radio.Button>
          <Radio.Button value={true}>是</Radio.Button>
        </Radio.Group>)}
      </FormItem>,

      <FormItem key="url" {...this.formLayout} label="远程url">
        {form.getFieldDecorator('url', {
          rules: [{ validator:(rule, value, callback)=>{
              if(!form.getFieldValue('isLocalProject') && !value){
                callback('请输入远程url！')
              }else{
                callback();
              }
            }, message: '请输入远程url！' }],
          initialValue: formVals.url,
        })(<Input placeholder="请输入" />)}
      </FormItem>,
      <FormItem key="runJobClass" {...this.formLayout} label="运行class">
        {form.getFieldDecorator('runJobClass', {
          rules: [{ required: true, message: '运行class名！' }],
          initialValue: formVals.runJobClass,
        })(<Input placeholder="请输入运行class" />)}
      </FormItem>,
      <FormItem key="param" {...this.formLayout} label="运行参数(json)">
        {form.getFieldDecorator('param', {
          rules: [{ validator:(rule, value, callback)=>{
              if(value){
                let flag = true;
                try{
                  var obj=JSON.parse(value);
                  if(typeof obj == 'object' && obj ){
                    flag = true;
                  }else{
                    flag = false;
                  }
                }catch (e) {
                  flag = false;

                }
                if(flag) {
                  callback();
                }else{
                  callback('请正确的json！')
                }

              }else{

                callback();
              }
            }, message: '请正确的json！' }],
          initialValue: formVals.param?JSON.stringify(formVals.param):null,
        })(<Input.TextArea rows={4} placeholder="请输入" />)}
      </FormItem>,

      <FormItem key="description" {...this.formLayout} label="描述">
        {form.getFieldDecorator('description', {
          initialValue: formVals.description,
        })(<Input placeholder="请输入描述" />)}
      </FormItem>,
      <FormItem key="state" {...this.formLayout} label="是否启动" hasFeedback>
        {form.getFieldDecorator('state', {
          rules: [{ required: true, message: '请选择是否启动！' }],
          initialValue: formVals.state || 0,
        })(<Radio.Group >
          <Radio.Button value={0}>暂停</Radio.Button>
          <Radio.Button value={1}>启动</Radio.Button>
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
        title={values && values.id ?'修改任务':'添加任务'}
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
