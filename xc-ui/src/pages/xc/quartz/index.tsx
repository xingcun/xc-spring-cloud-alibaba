import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import moment from 'moment';
import {
  Row,
  Col,
  Card,
  Form,
  Input,
  Select,
  Icon,
  Button,
  DatePicker,
  message,
  Badge,
  Divider,
  Popconfirm,
} from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { SorterResult } from 'antd/es/table';
import StandardTable, { StandardTableColumnProps } from './components/StandardTable';
import { TableListItem, TableListPagination, ModelVo } from './data';
import { Dispatch } from 'redux';
import styles from './style.less';
import UpdateForm, { IFormValsType } from './components/UpdateForm';

const FormItem = Form.Item;
const { Option } = Select;
const getValue = (obj: { [x: string]: string[] }) =>
  Object.keys(obj)
    .map(key => obj[key])
    .join(',');


interface TableListProps extends FormComponentProps {
  dispatch: Dispatch<any>;
  loading: boolean;
  xcQuartz: any;
}

interface TableListState {
  updateModalVisible: boolean;
  expandForm: boolean;
  selectedRows: Array<TableListItem>;
  formValues: { [key: string]: string };
  stepFormValues: Partial<TableListItem>;
  expandFormStyle: string;
  pagination: Partial<TableListPagination>;
}

/* eslint react/no-multi-comp:0 */
@connect(
  ({
     xcQuartz,
    loading,
  }: {
    xcQuartz: any;
    loading: {
      models: {
        [key: string]: boolean;
      };
    };
  }) => ({
    xcQuartz,
    loading: loading.models.rule,
  }),
)
class XQuartzTableList extends Component<TableListProps, TableListState> {
  state: TableListState = {
    updateModalVisible: false,
    expandForm: false,
    selectedRows: [],
    formValues: {},
    stepFormValues: {},
    expandFormStyle: null,
  };

  columns: StandardTableColumnProps[] = [
    {
      title: '工作名称',
      dataIndex: 'name',
    },
    {
      title: '运行cron',
      dataIndex: 'cron',
    },
    {
      title: '运行url',
      dataIndex: 'url',
    },
    {
      title: '运行状态',
      dataIndex: 'state',

      render(val: any) {
        if(val === 1){
          return <Badge status="success" text="运行" />;
        }else{
          return <Badge status="error" text='暂停' />;
        }

      },
    },

    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (val: string) => <span>{moment(val).format('YYYY-MM-DD HH:mm:ss')}</span>,
    },
    {
      title: '描述',
      dataIndex: 'description',
    },
    {
      title: '操作',
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdateModalVisible(true, record)}>修改</a>
          <Divider type="vertical" />
          <Popconfirm
            title={`是否设置任务${record.state ===1 ? '关闭' : '开启'}?`}
            onConfirm={e => {
              this.handleUpdate({
                id: record.id,
                state: record.state==1?0:1,
              });
              // message.success('Click on Yes');
            }}
            okText="是"
            cancelText="否"
          >
            <a href="#">{record.state ===1 ? '关闭' : '开启'}</a>
          </Popconfirm>
          <Divider type="vertical" />
          <Popconfirm
            title={`删除任务?`}
            onConfirm={e => {
              const { dispatch } = this.props;
              dispatch({
                type: 'xcQuartz/remove',
                payload: {
                  id: record.id
                },
                callback: msg => {
                  if (msg) {
                    message.error(msg);
                  } else {
                    message.success('删除成功');
                    this.handleUpdateModalVisible();
                    this.handleSearch();
                  }
                },
              });
              // message.success('Click on Yes');
            }}
            okText="是"
            cancelText="否"
          >
            <a href="#">删除</a>
          </Popconfirm>
        </Fragment>
      ),
    },
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'xcQuartz/fetch',
      payload: {
        pageSize: 10,
        pageNo: 1,
      },
    });
  }

  handleStandardTableChange = (
    pagination: Partial<TableListPagination>,
    filtersArg: Record<keyof TableListItem, string[]>,
    sorter: SorterResult<TableListItem>,
  ) => {
    const { dispatch } = this.props;
    const { formValues } = this.state;

    const filters = Object.keys(filtersArg).reduce((obj, key) => {
      const newObj = { ...obj };
      newObj[key] = getValue(filtersArg[key]);
      return newObj;
    }, {});
    const params: Partial<ModelVo> = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      input: formValues,
    };
    if (sorter.field) {
      params.sorter = `${sorter.field}_${sorter.order}`;
    }
    this.setState({
      pagination: pagination,
    });
    dispatch({
      type: 'xcQuartz/fetch',
      payload: params,
    });
  };

  handleFormReset = () => {
    const { form, dispatch } = this.props;
    form.resetFields();
    this.setState({
      formValues: {},
    });
  };

  toggleForm = () => {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
      expandFormStyle: expandForm ? 'none' : null,
    });
  };


  handleSelectRows = (rows: TableListItem[]) => {
    this.setState({
      selectedRows: rows,
    });
  };

  handleSearch = (e: React.FormEvent) => {
    if (e) {
      e.preventDefault();
    }

    const { dispatch, form } = this.props;

    form.validateFields((err, fieldsValue) => {
      if (err) return;

      const values = {
        ...fieldsValue,
        updatedAt: fieldsValue.updatedAt && fieldsValue.updatedAt.valueOf(),
      };

      this.setState({
        formValues: values,
      });
      console.log(this.state);
      dispatch({
        type: 'xcQuartz/fetch',
        payload: {
          input: values,
          pageNo: 1,
          pageSize: (this.state.pagination && this.state.pagination.pageSize) || 10,
        },
      });
    });
  };

  handleUpdateModalVisible = (flag?: boolean, record?: IFormValsType) => {
    this.setState({
      updateModalVisible: !!flag,
      stepFormValues: record || {},
    });
  };

  handleUpdate = (fields: IFormValsType) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'xcQuartz/update',
      payload: fields,
      callback: msg => {
        if (msg) {
          message.error(msg);
        } else {
          message.success('配置成功');
          this.handleUpdateModalVisible();
          this.handleSearch();
        }
      },
    });
  };

  renderSimpleForm() {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={8} sm={24}>
            <FormItem label="工作名">
              {getFieldDecorator('name')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={8} sm={24} style={{ display: this.state.expandFormStyle }}>
            <FormItem label="状态">
              {getFieldDecorator('state')(
                <Select placeholder="请选择" style={{ width: '100%' }} allowClear={true}>
                  <Option value="0">暂停</Option>
                  <Option value="1">启动</Option>
                </Select>,
              )}
            </FormItem>
          </Col>
          <Col md={8} sm={24} style={{ display: this.state.expandFormStyle }}>
            <FormItem label="创建日期">
              {getFieldDecorator('_beginTime')(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder="请输入创建日期"
                  format={'YYYY-MM-DD'}
                />,
              )}
            </FormItem>
          </Col>

          <Col md={8} sm={24} style={{ display: this.state.expandFormStyle }}>
            <FormItem label="是否在服务运行">
              {getFieldDecorator('isLocalProject')(
                <Select placeholder="请选择" style={{ width: '100%' }} allowClear={true}>
                  <Option value="true">是</Option>
                  <Option value="false">否</Option>
                </Select>,
              )}
            </FormItem>
          </Col>
          <Col md={8} sm={24}>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                查询
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                重置
              </Button>
              <a style={{ marginLeft: 8 }} onClick={this.toggleForm}>
                {this.state.expandFormStyle ? '展开' : '收起'}{' '}
                <Icon type={this.state.expandFormStyle ? 'down' : 'up'} />
              </a>
            </span>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      xcQuartz: { data },
      loading,
      form,
    } = this.props;
    const { selectedRows, updateModalVisible, stepFormValues } = this.state;

    const updateMethods = {
      handleUpdateModalVisible: this.handleUpdateModalVisible,
      handleUpdate: this.handleUpdate,
    };
    return (
      <div>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListForm}>{this.renderSimpleForm()}</div>
            <div className={styles.tableListOperator}>
              <Button
                icon="plus"
                type="primary"
                onClick={() => this.handleUpdateModalVisible(true, { valid: true })}
              >
                新建
              </Button>

            </div>
            <StandardTable
              selectedRows={selectedRows}
              loading={loading}
              data={data}
              rowKey={'id'}
              columns={this.columns}
              onSelectRow={this.handleSelectRows}
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
        {stepFormValues && Object.keys(stepFormValues).length ? (
          <UpdateForm
            {...updateMethods}
            updateModalVisible={updateModalVisible}
            values={stepFormValues}
          />
        ) : null}
      </div>
    );
  }
}

export default Form.create<TableListProps>()(XQuartzTableList);
