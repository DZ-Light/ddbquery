import {Form,Button} from "react-bootstrap";

export default function Query() {
    return (
        <Form className="needs-validation" noValidate>
            <div className="row g-3">
                <Form.Group className="col-sm-4">
                    <Form.Label htmlFor="select" className="form-label">数据源</Form.Label>
                    <Form.Select className="select2-search form-select" id="select" name="select" aria-label="Default select example"
                            aria-hidden="true">
                        <option value="">请选择</option>
                    </Form.Select>
                </Form.Group>

                <Form.Group className="col-sm-4">
                    <Form.Label htmlFor="env" className="form-label">环境</Form.Label>
                    <Form.Select className="select2-env form-select" id="env" name="env" aria-label="Default select example"
                            aria-hidden="true" >
                    </Form.Select>
                </Form.Group>
                <Form.Group className="col-sm-4">
                    <Form.Label htmlFor="sql" className="form-label">常用脚本</Form.Label>
                    <Form.Select className="select2-sql form-select" id="sql" name="sql" aria-label="Default select example"
                            aria-hidden="true" >
                    </Form.Select>
                </Form.Group>
                <div className="col-sm-4">
                    <Button className="w-100 btn btn-outline-primary btn-sm" type="button">
                        修改数据源
                    </Button>
                </div>
                <div className="col-sm-4">
                    <Button className="w-100 btn btn-outline-primary btn-sm" type="button">
                        刷新提示
                    </Button>
                </div>
                <div className="col-sm-4">
                    <Button className="w-100 btn btn-outline-primary btn-sm" type="button" >
                        <span id="boot">新增脚本</span>
                    </Button>
                </div>
                <div className="col-12">
                    <label htmlFor="code" className="form-label">SQL脚本</label>
                    <textarea className="form-control" id="code" name="info" ></textarea>
                </div>
                <Button className="w-100 btn btn-primary btn-lg" type="button" >查询
                </Button>
            </div>
        </Form>
    )
}
