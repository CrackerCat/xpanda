var path = require('path')
module.exports = {
    entry: './src/cmd_simplify.ts',
    output: {
        path:path.join(__dirname,'../resources'),
        filename: 'cmd.js',
    },
    target:['node'],
    resolve: {
        extensions: ['.js','.ts']
    },
    optimization: {
        minimize: true,
        minimizer: [
            (compiler) => {
                const TerserPlugin = require('terser-webpack-plugin');
                new TerserPlugin({
                    extractComments: false
                }).apply(compiler);
            },
        ]
    },
    module: {
        rules: [
            {
                // test指定规则生效文件
                test:/\.ts$/,
                use:'ts-loader',
                // 排除文件夹
                exclude:/node-modules/
            }
        ]
    },
    mode:"production"
}