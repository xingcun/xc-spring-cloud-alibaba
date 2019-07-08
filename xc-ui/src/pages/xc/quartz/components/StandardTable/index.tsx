import React, { Component, Fragment } from 'react';
import { Table, Alert } from 'antd';
import { TableProps, ColumnProps, SorterResult } from 'antd/es/table';
import styles from './index.less';
import { TableListItem } from '../../data';

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

export interface StandardTableProps<T> extends Omit<TableProps<T>, 'columns'> {
  columns: StandardTableColumnProps[];
  data: {
    list: Array<TableListItem>;
    pagination: StandardTableProps<TableListItem>['pagination'];
  };
  selectedRows: TableListItem[];
  onSelectRow: (rows: any) => void;
}

export type StandardTableColumnProps = ColumnProps<TableListItem> & {
  needTotal?: boolean;
  total?: number;
};

function initTotalList(columns: StandardTableColumnProps[]) {
  if (!columns) {
    return [];
  }
  const totalList: StandardTableColumnProps[] = [];
  columns.forEach(column => {
    if (column.needTotal) {
      totalList.push({ ...column, total: 0 });
    }
  });
  return totalList;
}

interface StandardTableState {
  selectedRowKeys: string[];
  needTotalList: StandardTableColumnProps[];
}

class StandardTable extends Component<StandardTableProps<TableListItem>, StandardTableState> {
  static getDerivedStateFromProps(nextProps: StandardTableProps<TableListItem>) {
    // clean state
    if (nextProps.selectedRows.length === 0) {
      const needTotalList = initTotalList(nextProps.columns);
      return {
        selectedRowKeys: [],
        needTotalList,
      };
    }
    return null;
  }
  constructor(props: StandardTableProps<TableListItem>) {
    super(props);
    const { columns } = props;
    const needTotalList = initTotalList(columns);

    this.state = {
      selectedRowKeys: [],
      needTotalList,
    };
  }

  handleRowSelectChange = (selectedRowKeys: string[], selectedRows: TableListItem[]) => {
    let { needTotalList } = this.state;
    needTotalList = needTotalList.map(item => ({
      ...item,
      total: selectedRows.reduce((sum, val) => sum + parseFloat(val[item.dataIndex], 10), 0),
    }));
    const { onSelectRow } = this.props;
    if (onSelectRow) {
      onSelectRow(selectedRows);
    }

    this.setState({ selectedRowKeys, needTotalList });
  };

  handleTableChange = (
    pagination: StandardTableProps<TableListItem>['pagination'],
    filters: Record<keyof TableListItem, string[]>,
    sorter: SorterResult<TableListItem>,
    ...rest
  ) => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(pagination, filters, sorter, ...rest);
    }
  };

  cleanSelectedKeys = () => {
    this.handleRowSelectChange([], []);
  };

  render() {
    const { selectedRowKeys, needTotalList } = this.state;
    const { data, rowKey, ...rest } = this.props;
    let {  pagination = false } = data || {};
    let list =[];
    if(data.result) {
      list = data.result.list || [];
      pagination = {
        pageSize: data.pageSize,
        current: data.pageNo,
        total: data.total || 0
      }
    }
    const paginationProps = {
      showSizeChanger: true,
      showQuickJumper: true,
      hideOnSinglePage: false,
      showTotal: (total, range)=>{
        return `${range[0]}-${range[1]} of ${total} items`
      },
      ...pagination,
    };


    return (
      <div className={styles.standardTable}>
        <Table
          rowKey={rowKey || 'key'}
          dataSource={list}
          pagination={paginationProps}
          onChange={this.handleTableChange}
          {...rest}
        />
      </div>
    );
  }
}

export default StandardTable;
