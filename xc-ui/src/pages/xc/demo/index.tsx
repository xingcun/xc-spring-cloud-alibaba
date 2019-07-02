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
  Dropdown,
  Menu,
  InputNumber,
  DatePicker,
  message,
  Badge,
  Divider,
  Popconfirm,
} from 'antd';
import { FormComponentProps } from 'antd/es/form';
import { SorterResult } from 'antd/es/table';
import StandardTable, { StandardTableColumnProps } from './components/StandardTable';
import { TableListItem, TableListParams, TableListPagination, ModelVo } from './data';
import { Dispatch } from 'redux';
import styles from './style.less';
import UpdateForm, { IFormValsType } from './components/UpdateForm';
import { PageHeaderWrapper } from '@ant-design/pro-layout';

const FormItem = Form.Item;
const { Option } = Select;
const getValue = (obj: { [x: string]: string[] }) =>
  Object.keys(obj)
    .map(key => obj[key])
    .join(',');

type IStatusMapType = 'default' | 'processing' | 'success' | 'error';
const status = ['后台', 'app'];

interface TableListProps extends FormComponentProps {
  dispatch: Dispatch<any>;
  loading: boolean;
  xcUserList: any;
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
    xcUserList,
    loading,
  }: {
    xcUserList: any;
    loading: {
      models: {
        [key: string]: boolean;
      };
    };
  }) => ({
    xcUserList,
    loading: loading.models.rule,
  }),
)
class XcUserTableList extends Component<TableListProps, TableListState> {
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
      title: '用户名',
      dataIndex: 'userName',
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
    },
    {
      title: '昵称',
      dataIndex: 'nickName',
    },
    {
      title: '来源',
      dataIndex: 'source',
      filters: [
        {
          text: status[0],
          value: '0',
        },
        {
          text: status[1],
          value: '1',
        },
      ],
      render(val: IStatusMapType) {
        return <Badge status="success" text={status[val]} />;
      },
    },

    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (val: string) => <span>{moment(val).format('YYYY-MM-DD HH:mm:ss')}</span>,
    },
    {
      title: '操作',
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdateModalVisible(true, record)}>修改</a>
          <Divider type="vertical" />
          <Popconfirm
            title={`是否设置用户${record.valid ? '失' : '有'}效?`}
            onConfirm={e => {
              this.handleUpdate({
                id: record.id,
                valid: !record.valid,
              });
              // message.success('Click on Yes');
            }}
            okText="是"
            cancelText="否"
          >
            <a href="#">{record.valid ? '有' : '失'}效</a>
          </Popconfirm>
        </Fragment>
      ),
    },
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'xcUserList/fetch',
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
    console.log(filters);
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
      type: 'xcUserList/fetch',
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

  handleMenuClick = (e: { key: string }) => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;

    if (!selectedRows) return;
    switch (e.key) {
      case 'remove':
        dispatch({
          type: 'xcUserList/remove',
          payload: {
            key: selectedRows.map(row => row.key),
          },
          callback: () => {
            this.setState({
              selectedRows: [],
            });
          },
        });
        break;
      default:
        break;
    }
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
        type: 'xcUserList/fetch',
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
      type: 'xcUserList/update',
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
            <FormItem label="用户名">
              {getFieldDecorator('userName')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={8} sm={24}>
            <FormItem label="手机号">
              {getFieldDecorator('mobile')(<Input placeholder="请输入" />)}
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
            <FormItem label="来源类型">
              {getFieldDecorator('source')(
                <Select placeholder="请选择" style={{ width: '100%' }} allowClear={true}>
                  <Option value="0">后台</Option>
                  <Option value="1">app</Option>
                </Select>,
              )}
            </FormItem>
          </Col>
          <Col md={8} sm={24} style={{ display: this.state.expandFormStyle }}>
            <FormItem label="是否有效">
              {getFieldDecorator('valid')(
                <Select placeholder="请选择" style={{ width: '100%' }} allowClear={true}>
                  <Option value="true">有效</Option>
                  <Option value="false">无效</Option>
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
      xcUserList: { data },
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
              {selectedRows.length > 0 && (
                <span>
                  <Button>批量操作</Button>
                </span>
              )}
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

export default Form.create<TableListProps>()(XcUserTableList);
