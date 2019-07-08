export interface TableListItem {
  id: string;
  userName: string;
  password?: string;
  nickName?: string;
  mobile: string;
  source: number;
  valid: boolean;
}

export interface TableListPagination {
  total: number;
  pageSize: number;
  current: number;
}

export interface TableListDate {
  list: TableListItem[];
  pagination: Partial<TableListPagination>;
}

export interface ModelVo {
  orderBys: [];
  input: any;
  pageSize: number;
  pageNo: number;
  result: any;
}
