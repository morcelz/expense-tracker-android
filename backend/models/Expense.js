const mongoose = require('mongoose');

const expenseSchema = new mongoose.Schema({
    userId: { type: String, required: true },
    amount: { type: Number, required: true },
    description: { type: String, required: true },
    category: { type: String, required: true },
    date: { type: Number, required: true }
});

expenseSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) {
        ret.id = ret._id;
        delete ret._id;
    }
});

module.exports = mongoose.model('Expense', expenseSchema);
