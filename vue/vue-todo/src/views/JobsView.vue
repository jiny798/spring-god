<template>
    <div>
        <list-item></list-item>
    </div>
</template>

<script>
import ListItem from '../components/ListItem.vue';
import bus from '../utils/bus.js';
import ListMixin from '../mixins/ListMixin.js';
export default {
    components: {
        ListItem,
    },
    mixins: [ListMixin],
    created() {
        // this.$store.dispatch('FETCH_JOBS');
        // bus.$emit('start:spinner');
        // this.$store
        //     .dispatch('FETCH_JOBS')
        //     .then(() => {
        //         console.log('fetched');
        //         bus.$emit('end:spinner');
        //     })
        //     .catch((error) => {
        //         console.log(error);
        //     });
        // bus.$emit('end:spinner');
    },
    // mounted() {
    //     bus.$emit('end:spinner');
    // },
};
</script>

<style scoped></style>
